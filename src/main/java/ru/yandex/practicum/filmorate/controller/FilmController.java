package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;


    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return service.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {

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
        return service.getFilmsByDirector(directorId, sortBy);
    }

    //добавлена опция удаления фильма
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilm(@PathVariable Long id) {
        service.deleteFilm(id);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Long userId,
                                     @RequestParam Long friendId) {
        return service.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> searchFilm(@RequestParam(required = false) String query,
                                 @RequestParam(required = false) List<String> by) {
        return service.searchFilm(query, by);
    }

}