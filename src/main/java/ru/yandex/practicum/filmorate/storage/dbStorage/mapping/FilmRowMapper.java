package ru.yandex.practicum.filmorate.storage.dbStorage.mapping;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        Mpa mpa = new Mpa();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDuration(rs.getLong("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDescription(rs.getString("description"));
        mpa.setId(rs.getLong("rating_id"));
        film.setMpa(mpa);
        return film;
    }
}
