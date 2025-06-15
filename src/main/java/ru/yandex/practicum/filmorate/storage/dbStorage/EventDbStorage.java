package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.dbStorage.mapping.EventRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class EventDbStorage implements EventStorage {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EventRowMapper eventRowMapper;


    @Override
    public void create(Event event) {
        String query = "INSERT INTO feed (action_time,user_id,event_type,operation_type,entity_id) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(query, new String[]{"event_id"});
            stmt.setObject(1, event.getActionTime());
            stmt.setLong(2, event.getUserId());
            stmt.setString(3,event.getEventType().toString());
            stmt.setString(4,event.getOperationType().toString());
            stmt.setLong(5, event.getEntityId());
            return stmt;
        }, keyHolder);

        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;

        if (id == null) {
            throw new RuntimeException("Не удалось получить ID события после вставки");
        }
    }
    @Override
    public List<Event> getFeedForUser(Long userId) {
        String query = "SELECT * FROM feed";
        return jdbcTemplate.query(query, eventRowMapper);
    }
    }

