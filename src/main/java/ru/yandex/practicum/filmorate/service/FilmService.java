package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage storage;
    private final InMemoryUserStorage userStorage;
    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private long generateId = 0;


    public Film create(Film film) {
        validation(film);
        film.setId(++generateId);
        return storage.create(film);
    }

    public void validation(Film film) {
        if (film.getReleaseDate().isBefore(RELEASE_DATE)) {
            throw new ValidationException("Дата некорректна");
        }
    }

    public Film update(Film film) {
        validation(film);
        return storage.update(film);
    }

    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    public Film addLike(Long filmId, Long userId) {
        Film film = storage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        if (user == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        if (film == null) {
            throw new DataNotFoundException("Фильм не найден");
        }
        Set<Long> list = film.getLikeSet();
        list.add(userId);
        film.setLikeSet(list);
        return storage.update(film);
    }

    public Film deleteLike(Long filmId, Long userId) {
        Film film = storage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        if (film == null || user == null) {
            throw new DataNotFoundException("Фильм не найден");
        }
        Set<Long> list = film.getLikeSet();
        list.remove(userId);
        film.setLikeSet(list);
        return storage.update(film);
    }

    public List<Film> topFilms(Integer count) {
        if (count < 1) {
            throw new ValidationException("Некорректное значение размера");
        }
            return storage.topFilms(count);
    }

}
