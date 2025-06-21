package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    @Autowired
    private DirectorService service;


    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
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
        return service.update(director);
    }

    @DeleteMapping("/{id}")
    public Director delete(@PathVariable Long id) {
        return service.delete(id);
    }
}