package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.storage.dbStorage.EventStorage;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventStorage eventStorage;

    public void createEvent(OffsetDateTime actionTime, Long userId, EventType eventType, OperationType operationType, Long entityId) {
        Event event = new Event();
        event.setActionTime(actionTime);
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperationType(operationType);
        event.setEntityId(entityId);

        eventStorage.create(event);



    }
}
