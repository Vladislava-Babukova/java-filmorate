package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserHandler userHandler = new UserHandler();

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Начато создание пользователя.Получен объект{}", user);
        return userHandler.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Начато обновление Пользователя.Получен объект{}", user);
        return userHandler.update(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userHandler.getAllUsers();
    }
}