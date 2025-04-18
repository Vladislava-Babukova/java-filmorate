package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmHandler {

    private final Map<Long, Film> storageFilm = new HashMap<>();
    private long generateId = 0;
    private LocalDate date = LocalDate.of(1895, Month.DECEMBER, 28);

    public Film create(Film film) {
        if (film.getReleaseDate().isAfter(date)) {
            film.setId(++generateId);
            storageFilm.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Дата некорректна");
        }

    }

    public Film update(Film film) {
        if (film.getReleaseDate().isAfter(date)) {
            if (!storageFilm.containsKey(film.getId())) {
                throw new DataNotFoundException("Фильм не найден");
            }
            storageFilm.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Дата некорректна");

        }
    }

    public List<Film> getAllFilms() {
        return new ArrayList<Film>(storageFilm.values());
    }
}