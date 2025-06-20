package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dbStorage.mapping.GenreRowMapper;

import java.util.List;

@Repository
public class GenreDBStorage implements GenreStorage {
    private final GenreRowMapper genreRowMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public GenreDBStorage(GenreRowMapper genreRowMapper) {
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public List<Genre> getAllGenres() {
        String query = "SELECT * FROM genres";
        return jdbcTemplate.query(query, genreRowMapper);
    }

    @Override
    public Genre getGenre(Long genreId) {

        String query = "SELECT * FROM genres WHERE genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(query, genreRowMapper, genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("жанр с ID " + genreId + " не найден в таблице genres");
        }
    }
}
