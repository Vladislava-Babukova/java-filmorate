package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHandler {


    private final Map<Long, User> storageUser = new HashMap<>();
    private long generateId = 0;
    private LocalDate dateNow = LocalDate.now();

    public User create(User user) {
        if (user.getBirthday().isBefore(dateNow)) {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            user.setId(++generateId);
            storageUser.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("Дата рождения некорректна");
        }

    }

    public User update(User user) {
        if (user.getBirthday().isBefore(dateNow)) {
            if (!storageUser.containsKey(user.getId())) {
                throw new DataNotFoundException("Пользователь не найден");
            }
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            storageUser.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("Дата рождения некорректна");
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<User>(storageUser.values());
    }
}