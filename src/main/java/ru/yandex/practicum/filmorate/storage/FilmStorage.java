package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    public Film create(Film film);

    public Film update(Film film);

    public List<Film> getAllFilms();

    public Film getFilm(Long filmId);

    public List<Film> topFilms(int count);
}
