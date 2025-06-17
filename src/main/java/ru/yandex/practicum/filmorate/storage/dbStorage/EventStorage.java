package ru.yandex.practicum.filmorate.storage.dbStorage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    void create(Event event);

    List<Event> getFeedForUser(Long userId);

    void deleteUserEvents(Long userId);

    void deleteFilmEvents(Long filmId);
}
