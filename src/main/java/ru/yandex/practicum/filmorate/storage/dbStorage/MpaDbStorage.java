package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dbStorage.mapping.MpaRowMapper;

import java.util.List;

@Repository
public class MpaDbStorage implements MpaStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mpaRowMapper;

    public MpaDbStorage(MpaRowMapper mpaRowMapper) {
        this.mpaRowMapper = mpaRowMapper;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String query = "SELECT * FROM ratings";
        return jdbcTemplate.query(query, mpaRowMapper);
    }

    @Override
    public Mpa getMpa(Long id) {

        String query = "SELECT * FROM ratings WHERE rating_id = ?";
        try {
            return jdbcTemplate.queryForObject(query, mpaRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("рейтинг с ID " + id + " не найден");
        }
    }
}
