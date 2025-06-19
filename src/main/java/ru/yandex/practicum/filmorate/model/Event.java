package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.serializer.InstantMillisSerializer;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    @NotNull
    @JsonSerialize(using = InstantMillisSerializer.class)
    private Instant timestamp;
    @NotNull
    @Positive
    private Long userId;
    @NotNull
    private EventType eventType;
    @NotNull
    private OperationType operation;
    private Long eventId;
    @NotNull
    @Positive
    private Long entityId;
}