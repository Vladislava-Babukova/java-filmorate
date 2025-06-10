package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.mapping.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dbStorage.mapping.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dbStorage.mapping.MpaRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Repository

public class FilmDbStorage implements FilmStorage {
    @Autowired
    private FilmRowMapper filmRowMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private GenreStorage genreStorage;
    @Autowired
    private GenreRowMapper genreRowMapper;
    @Autowired
    private MpaRowMapper mpaRowMapper;

    public Film create(Film film) {
        if (film == null) {
            throw new IllegalArgumentException("Film не может быть null");
        }
        if (film.getReleaseDate() == null) {
            throw new IllegalArgumentException("ReleaseDate не может быть null");
        }

        String query = "INSERT INTO FILMS (name, description, release_date, duration, rating_id)" +
                "values(?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(query, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            if (film.getMpa() != null) {

                String checkMpaQuery = "SELECT COUNT(*) FROM ratings WHERE rating_id = ?";
                Integer mpaCount = jdbcTemplate.queryForObject(checkMpaQuery, Integer.class, film.getMpa().getId());

                if (mpaCount == null || mpaCount == 0) {
                    throw new DataNotFoundException("рэйтинг с ID " + film.getMpa().getId() + " не найден");
                }
                stmt.setLong(5, film.getMpa().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            return stmt;
        }, keyHolder);
        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;

        if (id == null) {
            throw new RuntimeException("Не удалось получить ID фильма после вставки");
        }
        film.setId(id);
        saveGenre(film);
        addMpa(film);
        return film;
    }

    public Film addMpa(Film film) {
        try {
            String query = "SELECT * FROM ratings WHERE rating_id = ?";
            Mpa mpa = jdbcTemplate.queryForObject(query, mpaRowMapper, film.getMpa().getId());
            film.setMpa(mpa);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("рейтинг с ID " + film.getMpa().getId() + " не найден");
        }
    }

    public void saveGenre(Film film) {
        Long filmId = film.getId();
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        if (!filmExists(filmId)) {
            throw new DataNotFoundException("Фильм с ID " + filmId + " не найден");
        }
        Set<Genre> uniqueGenres = new LinkedHashSet<>(film.getGenres());
        film.setGenres(new ArrayList<>(uniqueGenres));

        for (Genre genre : film.getGenres()) {
            if (!genreExists(genre.getId())) {
                throw new DataNotFoundException("Жанр с ID " + genre.getId() + " не найден");
            }
        }
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", filmId);

        String insertSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        List<Object[]> batchArgs = film.getGenres().stream()
                .map(genre -> new Object[]{filmId, genre.getId()})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(insertSql, batchArgs);

    }

    public List<Genre> addGenre(Film film) {
        try {
            String getGenresQuery = "SELECT g.* FROM genres g JOIN film_genres fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
            List<Genre> genres = jdbcTemplate.query(getGenresQuery, genreRowMapper, film.getId());
            return genres;
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("жанр с ID " + film.getId() + " не найден");
        }
    }

    @SneakyThrows
    @Override
    public Film update(Film film) {
        if (film == null) {
            throw new IllegalArgumentException("Film не может быть null");
        }
        if (film.getId() == null) {
            throw new IllegalArgumentException("ID фильма не может быть null");
        }

        if (!filmExists(film.getId())) {
            throw new SQLException("Фильм с ID " + film.getId() + " не найден");
        }


        updateFilmData(film);
        updateFilmGenres(film);

        return getFullFilmData(film.getId());
    }

    private boolean filmExists(Long filmId) {
        String sql = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filmId);
        return count != null && count > 0;
    }


    private void updateFilmData(Film film) {
        String filmSql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(filmSql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());
    }

    private void updateFilmGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (!genreExists(genre.getId())) {
                    throw new DataNotFoundException("Жанр с ID " + genre.getId() + " не найден");
                }
            }

            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sql,
                    film.getGenres(),
                    film.getGenres().size(),
                    (ps, genre) -> {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, genre.getId());
                    });
        }
    }

    private boolean genreExists(Long genreId) {
        String sql = "SELECT COUNT(*) FROM genres WHERE genre_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genreId);
        return count != null && count > 0;
    }

    private Film getFullFilmData(Long filmId) {
        String filmSql = "SELECT f.*, r.rating_name FROM films f LEFT JOIN ratings r ON f.rating_id = r.rating_id WHERE f.film_id = ?";
        Film film = jdbcTemplate.queryForObject(filmSql, (rs, rowNum) -> {
            Film f = new Film();
            f.setId(rs.getLong("film_id"));
            f.setName(rs.getString("name"));
            f.setDescription(rs.getString("description"));
            f.setReleaseDate(rs.getDate("release_date").toLocalDate());
            f.setDuration(rs.getLong("duration"));

            if (rs.getObject("rating_id") != null) {
                Mpa mpa = new Mpa();
                mpa.setId(rs.getLong("rating_id"));
                mpa.setName(rs.getString("rating_name"));
                f.setMpa(mpa);
            }
            return f;
        }, filmId);

        String genresSql = "SELECT g.* FROM genres g JOIN film_genres fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(genresSql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("genre_name"));
            return genre;
        }, filmId);
        film.setGenres(genres);
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String query = "SELECT * FROM films";
        return jdbcTemplate.query(query, filmRowMapper);
    }

    @Override
    public Film getFilm(Long filmId) {
        try {
            String query = "SELECT * FROM films WHERE film_id = ?";
            Film film = jdbcTemplate.queryForObject(query, filmRowMapper, filmId);
            film.setGenres(addGenre(film));
            addMpa(film);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("фильм с ID " + filmId + " не найден в таблице films");
        }
    }

    @Override
    public List<Film> topFilms(int count) {
        String query = "SELECT f.film_id " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC, f.film_id ASC " +
                "LIMIT ?";

        List<Long> filmIds = jdbcTemplate.query(query,
                (rs, rowNum) -> rs.getLong("film_id"),
                count);

        return filmIds.stream()
                .map(this::getFilm)
                .collect(Collectors.toList());
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        String checkQuery = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
        boolean alreadyLiked = jdbcTemplate.queryForObject(checkQuery, Integer.class, filmId, userId) > 0;

        if (!alreadyLiked) {
            String insertQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(insertQuery, filmId, userId);

            if (film.getLikeSet() == null) {
                film.setLikeSet(new HashSet<>());
            }
            film.getLikeSet().add(userId);
        }
        return film;
    }

    public Boolean checkIdLike(Long filmId, Long userId) {

        String checkFilm = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        Integer filmCount = jdbcTemplate.queryForObject(checkFilm, Integer.class, filmId);
        String checkUser = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer userCount = jdbcTemplate.queryForObject(checkUser, Integer.class, userId);
        if (userCount == null || userCount == 0 || filmCount == null || filmCount == 0) {
            throw new DataNotFoundException("Данные не найдены");
        }
        return true;
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        Set<Long> likeSet = film.getLikeSet();
        if (!checkIdLike(filmId, userId)) {
            throw new DataNotFoundException("запись не найдена");
        }
        String checkQuery = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkQuery, Integer.class, filmId, userId);
        if (count < 1) {
            throw new DataNotFoundException("запись не найдена");
        }
        String insertQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(insertQuery, filmId, userId);
        likeSet.remove(userId);
        return film;
    }
}

