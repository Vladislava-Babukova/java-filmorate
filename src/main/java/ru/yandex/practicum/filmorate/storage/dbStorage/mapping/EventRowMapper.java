package ru.yandex.practicum.filmorate.storage.dbStorage.mapping;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getLong("event_id"));
        event.setTimestamp(Instant.ofEpochMilli(rs.getLong("action_time")));
        event.setUserId(rs.getLong("user_id"));
        event.setEventType(EventType.valueOf(rs.getString("event_type")));
        event.setOperation(OperationType.valueOf(rs.getString("operation_type")));
        event.setEntityId(rs.getLong("entity_id"));
        return event;
    }
}