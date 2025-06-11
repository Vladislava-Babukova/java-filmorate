package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage storage;
    private final UserService userService;
    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private long generateId = 0;


    public Film create(Film film) {
        checkDate(film);
        return storage.create(film);
    }

    public void checkDate(Film film) {
        if (film.getReleaseDate().isBefore(RELEASE_DATE)) {
            throw new ValidationException("Дата некорректна");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание некорректно");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Длительность некорректна");
        }
    }

    public Film update(Film film) {
        checkDate(film);
        return storage.update(film);
    }

    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    public Film addLike(Long filmId, Long userId) {
        return storage.addLike(filmId, userId);
    }

    public Film deleteLike(Long filmId, Long userId) {
        return storage.deleteLike(filmId, userId);

    }

    public List<Film> topFilms(Integer count) {
        if (count < 1) {
            throw new ValidationException("Некорректное значение размера");
        }
        return storage.topFilms(count);
    }

    public Film getFilm(Long id) {
        return storage.getFilm(id);
    }

    //добавлена функция выдачи списка фильмов режисёра по его айди
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        return storage.getFilmsByDirector(directorId, sortBy);
    }
}
