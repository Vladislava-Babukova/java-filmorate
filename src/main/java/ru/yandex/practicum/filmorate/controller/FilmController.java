package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Начато создание фильма.Получен объект{}", film);
        return service.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Начато обновление фильма.Получен объект{}", film);
        return service.update(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return service.getAllFilms();
    }

    /* новый метод так же, как и старый, включает в себя параметр count,
    но при этом использует тот же путь для эндпоинта /popular */
    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) {

        return service.getPopularFilms(count, genreId, year);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        return service.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        return service.deleteLike(id, userId);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        return service.getFilm(id);
    }

    //добавлена функция выдачи списка фильмов режисёра по его айди
    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable Long directorId,
                                         @RequestParam(value = "sortBy", required = false, defaultValue = "year") String sortBy) {
        if (!sortBy.equals("year") && !sortBy.equals("likes")) {
            throw new IllegalArgumentException("Параметр sortBy должен быть 'year' или 'likes'");
        }
        return service.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Long userId,
                                     @RequestParam Long friendId) {
        return service.getCommonFilms(userId, friendId);
    }
}