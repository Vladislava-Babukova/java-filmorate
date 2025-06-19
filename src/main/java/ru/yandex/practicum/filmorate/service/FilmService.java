package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private final FilmStorage storage;
    private final UserService userService;
    private final EventService eventService;
    private final long generateId = 0;


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
//        if (!film.getLikeSet().isEmpty()) {
//            eventService.createEvent(Instant.now(), userId, EventType.LIKE, OperationType.ADD, filmId);
//        }
        List<Long> likes = storage.getLikesByFilm(filmId);
        long countLikes = likes.size();
        Film film = storage.addLike(filmId, userId);
//        if (storage.getLikesByFilm(filmId).size() == countLikes + 1){
            eventService.createEvent(Instant.now(), userId, EventType.LIKE, OperationType.ADD, filmId);
//        }
//        for (Long l : likes){
//            if (Objects.equals(l, filmId)) {
//                eventService.createEvent(Instant.now(), userId, EventType.LIKE, OperationType.ADD, filmId);
//            }
//        }
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        Film film = storage.deleteLike(filmId, userId);
//        if (film.getLikeSet().isEmpty()) {
            eventService.createEvent(Instant.now(), userId, EventType.LIKE, OperationType.REMOVE, filmId);
//        }
        return film;
    }

    // добавлены необходимые для новой логики параметры метода

    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        if (count < 1) {
            throw new ValidationException("Некорректное значение count");
        }
        return storage.getPopularFilms(count, genreId, year);
    }

    public Film getFilm(Long id) {
        return storage.getFilm(id);
    }

    //добавлена функция выдачи списка фильмов режисёра по его айди
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        return storage.getFilmsByDirector(directorId, sortBy);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return storage.getCommonFilms(userId, friendId);
    }

    public void deleteFilm(Long id) {
//        eventService.deleteFilmEvents(id);
        storage.deleteFilm(id);
    }

    public List<Film> searchFilm(String query, List<String> by) {
        return storage.searchFilm(query, by);
    }

}
