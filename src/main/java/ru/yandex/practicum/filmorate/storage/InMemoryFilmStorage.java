package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistExeption;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> storageFilm = new HashMap<>();

    public Film create(Film film) {
        if (exists(film)) {
            throw new DataAlreadyExistExeption("Данный фильм уже существует");
        }
        storageFilm.put(film.getId(), film);
        return film;
    }

    private boolean exists(Film film) {
        if (storageFilm.containsKey(film.getId())) {
            return true;
        } else {
            return false;
        }
    }

    public Film update(Film film) {
        if (!exists(film)) {
            throw new DataNotFoundException("Фильм не найден");
        }
        storageFilm.put(film.getId(), film);
        return film;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<Film>(storageFilm.values());
    }

    public Film getFilm(Long filmId) {
        return storageFilm.get(filmId);
    }

    public List<Film> topFilms(int count) {

        return storageFilm.values().stream()
                .sorted(Comparator.comparingInt(film -> ((Film) film).getLikeSet().size()).reversed())
                .limit(count)
                .toList();
    }

}
