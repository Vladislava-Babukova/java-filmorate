package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.mapping.DirectorRowMapper;
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
    @Autowired
    private DirectorRowMapper directorRowMapper;

    //метод изменён, добавлен режисёр
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
        saveDirector(film);
        addDirector(film);
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

    //метод по сохранению режисёров фильма в таблицу
    public void saveDirector(Film film) {
        Long filmId = film.getId();
        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            return;
        }

        if (!filmExists(filmId)) {
            throw new DataNotFoundException("Фильм с ID " + filmId + " не найден");
        }
        Set<Director> uniqueDirector = new LinkedHashSet<>(film.getDirectors());
        film.setDirectors(new ArrayList<>(uniqueDirector));

        for (Director director : film.getDirectors()) {
            if (!directorExists(director.getId())) {
                throw new DataNotFoundException("Режисёр с ID " + director.getId() + " не найден");
            }
        }
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", filmId);

        String insertSql = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

        List<Object[]> batchArgs = film.getDirectors().stream()
                .map(director -> new Object[]{filmId, director.getId()})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(insertSql, batchArgs);

    }

    //метод по проверке существования режисёра
    private boolean directorExists(Long directorId) {
        String sql = "SELECT COUNT(*) FROM directors WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, directorId);
        return count != null && count > 0;
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

    //метод по получению режисёров фильма из таблицы
    public List<Director> addDirector(Film film) {
        try {
            String getDirectorQuery = "SELECT d.* FROM directors d JOIN film_directors fd ON d.id = fd.director_id WHERE fd.film_id = ?";
            List<Director> directors = jdbcTemplate.query(getDirectorQuery, directorRowMapper, film.getId());
            return directors;
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Режисёр с ID " + film.getId() + " не найден");
        }
    }

    //изменён метод, добавлен режисёр
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
        updateFilmDirectors(film);

        return getFilm(film.getId());
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

    //изменён метод обновления жанра для фильма
    private void updateFilmGenres(Film film) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        saveGenre(film);
    }

    private void updateFilmDirectors(Film film) {
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());
        saveDirector(film);
    }

    private boolean genreExists(Long genreId) {
        String sql = "SELECT COUNT(*) FROM genres WHERE genre_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genreId);
        return count != null && count > 0;
    }

    @Override
    public List<Film> getAllFilms() {
        String query = "SELECT * FROM films";
        return jdbcTemplate.query(query, filmRowMapper);
    }

    //в метод добавлена строчка, добавляющая в фильм режисёра
    @Override
    public Film getFilm(Long filmId) {
        try {
            String query = "SELECT * FROM films WHERE film_id = ?";
            Film film = jdbcTemplate.queryForObject(query, filmRowMapper, filmId);
            film.setGenres(addGenre(film));
            film.setDirectors(addDirector(film));
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

    //добавлен метод выдачи фильмов по айди режиссёра
    @Override
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {

        String queryLike = "SELECT f.* " +
                "FROM films AS f " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.film_id) DESC;";


        String queryYear = "SELECT f.* " +
                "FROM films AS f " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "WHERE fd.director_id = ? " +
                "ORDER BY f.release_date ;";

        List<Film> filmByDirectors;
        if (sortBy.equals("likes")) {
            filmByDirectors = jdbcTemplate.query(queryLike, filmRowMapper, directorId);
        } else {
            filmByDirectors = jdbcTemplate.query(queryYear, filmRowMapper, directorId);
        }
        filmByDirectors.stream()
                .peek(film -> {
                    film.setDirectors(addDirector(film));
                    film.setGenres(addGenre(film));
                })
                .collect(Collectors.toUnmodifiableList());
        return filmByDirectors;
    }
}

