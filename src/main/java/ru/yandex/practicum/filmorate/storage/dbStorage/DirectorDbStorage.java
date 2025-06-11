package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.mapping.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class DirectorDbStorage implements DirectorStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DirectorRowMapper directorRowMapper;
    @Autowired
    private FilmStorage filmStorage;

    @Override
    public Director create(Director director) {
        String query = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;

        if (id == null) {
            throw new RuntimeException("Не удалось получить ID режисёра после вставки");
        }
        director.setId(id);
        return director;
    }

    @Override
    public Director getDirector(Long id) {
        try {
            String query = "SELECT * FROM directors WHERE id = ?";
            return jdbcTemplate.queryForObject(query, directorRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("режисёр с ID " + id + " не найден");
        }
    }

    @Override
    public List<Director> getAll() {
        try {
            String query = "SELECT * FROM directors";
            List<Director> directorList = jdbcTemplate.query(query, directorRowMapper);
            return directorList;
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("режисёры не найдены");
        }
    }

    @Override
    public Director update(Director director) {
        String checkIdQuery = "SELECT COUNT(*) FROM directors WHERE id = ?";
        Integer idCount = jdbcTemplate.queryForObject(checkIdQuery, Integer.class, director.getId());
        if (idCount == null || idCount == 0) {
            throw new DataNotFoundException("режисёр с ID " + director.getId() + " не найден");
        }

        String query = "UPDATE directors SET " +
                       "name = ? " +
                       "WHERE id = ?";
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(query, new String[]{"id"});
            stmt.setLong(2, director.getId());
            stmt.setString(1, director.getName());
            return stmt;
        });
        return director;
    }

    @Override
    public Director delete(Long id) {
        Director director = getDirector(id);
        if (director == null) {
            throw new DataNotFoundException("Режисёр не найден");
        }

        String insertQuery = "DELETE FROM directors WHERE (id = ?)";
        jdbcTemplate.update(insertQuery, id);
        return director;
    }
}
