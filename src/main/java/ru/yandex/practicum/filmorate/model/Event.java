package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    private Long id;
    @NotNull
    private OffsetDateTime actionTime;
    @NotNull
    @Positive
    private Long userId;
    @NotNull
    private EventType eventType;
    @NotNull
    private OperationType operationType;
    @NotNull
    @Positive
    private Long entityId;

//    public Event(OffsetDateTime actionTime, Long userId, EventType eventType, OperationType operationType, Long entityId) {
//        this.actionTime = actionTime;
//        this.userId = userId;
//        this.eventType = eventType;
//        this.operationType = operationType;
//        this.entityId = entityId;
//    }

//    public Event(OffsetDateTime now, Long userId, EventType eventType, OperationType operationType, Long friendId) {
//        System.out.println("создается ивент, юзер айди = " + userId);
//    }
}
