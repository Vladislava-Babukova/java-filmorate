package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    List<Film> getAllFilms();

    Film getFilm(Long filmId);

    List<Film> topFilms(int count);

    void saveGenre(Film film);

    Film addMpa(Film film);

    List<Genre> addGenre(Film film);

    Film addLike(Long filmId, Long userId);

    Film deleteLike(Long filmId, Long userId);

}
