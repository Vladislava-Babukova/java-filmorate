package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.storage.dbStorage.EventStorage;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;

    public void createEvent(Instant timestamp, Long userId, EventType eventType, OperationType operation, Long entityId) {
        Event event = new Event();
        event.setTimestamp(timestamp);
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setEntityId(entityId);
        eventStorage.create(event);
    }
    public void deleteUserEvents(Long userId) {
        eventStorage.deleteUserEvents(userId);
    }

    public void deleteFilmEvents(Long filmId) {
        eventStorage.deleteFilmEvents(filmId);
    }
}