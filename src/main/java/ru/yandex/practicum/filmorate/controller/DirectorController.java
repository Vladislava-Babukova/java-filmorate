package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    @Autowired
    private DirectorService service;

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Начато создание Режисёра.Получен объект{}", director);
        if (director == null) {
            throw new IllegalArgumentException("director не может быть null");
        }
        return service.create(director);
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable Long id) {
        return service.getDirector(id);
    }

    @GetMapping
    public List<Director> getAll() {
        return service.getAll();
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("Начато обновление режисёра.Получен объект{}", director);
        return service.update(director);
    }

    @DeleteMapping("/{id}")
    public Director delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
