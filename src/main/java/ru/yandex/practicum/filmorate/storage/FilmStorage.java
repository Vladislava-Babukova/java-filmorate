package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmStorage {
    public Film create(Film film);

    public Film update(Film film);

    public List<Film> getAllFilms();

    public Film getFilm(Long filmId);

    public List<Film> topFilms(int count);

    public void saveGenre(Film film);

    public Film addMpa(Film film);

    public List<Genre> addGenre(Film film);

    public Film addLike(Long filmId, Long userId);

    public Film deleteLike(Long filmId, Long userId);

}
