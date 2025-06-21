package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.mapping.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserDbStorage implements UserStorage {


    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRowMapper userRowMapper;

    @Override
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user не может быть null");
        }

        String query = "INSERT INTO users (name, login, email, birthday)" +
                "values(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(query, new String[]{"user_id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            stmt.setString(3, user.getEmail());
            return stmt;
        }, keyHolder);
        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;

        if (id == null) {
            throw new DataNotFoundException("Не удалось получить ID пользователя после вставки");
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (user == null) {
            throw new ValidationException("user не может быть null");
        }
        String checkIdQuery = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer idCount = jdbcTemplate.queryForObject(checkIdQuery, Integer.class, user.getId());

        if (idCount == null || idCount == 0) {
            throw new DataNotFoundException("пользователь с ID " + user.getId() + " не найден");
        }
        String query = "UPDATE users SET " +
                "name = ?, " +
                "login = ?, " +
                "email = ?, " +
                "birthday = ? " +
                "WHERE user_id = ?";

        jdbcTemplate.update(query,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String query = "SELECT * FROM users";
        return jdbcTemplate.query(query, userRowMapper);
    }

    @Override
    public User getUser(Long id) {
        try {
            String query = "SELECT * FROM users WHERE user_id = ?";
            return jdbcTemplate.queryForObject(query, userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("пользователь с ID " + id + " не найден");
        }
    }

    public User addFriend(Long userId, Long friendId) {

        if (!checkId(userId) || !checkId(friendId)) {
            throw new DataNotFoundException("запись не найдена");
        }
        String checkQuery = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?";
        Long count = Long.valueOf(jdbcTemplate.queryForObject(checkQuery, Integer.class, userId, friendId));
        if (count == 0) {
            String insertQuery = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
            jdbcTemplate.update(insertQuery, userId, friendId);
        }
        User user = getUser(userId);
        Set<User> friendSet = getFriends(userId);
        user.setFrendSet(friendSet);
        return user;
    }

    public Boolean checkId(Long userId) {
        String checkUser = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer userCount = jdbcTemplate.queryForObject(checkUser, Integer.class, userId);
        return userCount != null && userCount != 0;
    }


    public Set<User> getFriends(Long userId) {
        if (!checkId(userId)) {
            throw new DataNotFoundException("запись не найдена");
        }
        String friendSgl = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
        List<User> friendsList = jdbcTemplate.query(friendSgl, userRowMapper, userId);
        return new HashSet<>(friendsList);

    }

    public User deleteFriend(Long userId, Long friendId) {
        if (!checkId(userId) || !checkId(friendId)) {
            throw new DataNotFoundException("запись не найдена");
        }
        String checkQuery = "SELECT COUNT(*) FROM friends WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        Long count = (jdbcTemplate.queryForObject(checkQuery, Long.class, userId, friendId, friendId, userId));
        String insertQuery = "DELETE FROM friends WHERE (user_id = ? AND friend_id = ?)";
        jdbcTemplate.update(insertQuery, userId, friendId);
        User user = getUser(userId);
        user.setFrendSet(getFriends(userId));
        update(user);
        return user;
    }

    public Set<User> getCommonFriends(Long userId, Long friendId) {
        if (!checkId(userId) || !checkId(friendId)) {
            throw new DataNotFoundException("Один из пользователей не найден");
        }
        String sql = "SELECT u.* FROM users u\n" +
                "        JOIN friends f1 ON u.user_id = f1.friend_id AND f1.user_id = ?\n" +
                "        JOIN friends f2 ON u.user_id = f2.friend_id AND f2.user_id = ?";

        List<User> commonFriends = jdbcTemplate.query(sql, userRowMapper, userId, friendId);

        return new HashSet<>(commonFriends);
    }

    @Override
    public List<Long> findUsersWithSimilarTastes(Long userId) {
        String sql = "SELECT l2.user_id " +
                "FROM likes l1 " +
                "JOIN likes l2 ON l1.film_id = l2.film_id AND l2.user_id != l1.user_id " +
                "WHERE l1.user_id = ? " +
                "GROUP BY l2.user_id " +
                "ORDER BY COUNT(l2.film_id) DESC " +
                "LIMIT 1";

        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }

    @Override
    public List<Long> getFilmsLikedByUser(Long userId) {
        String sql = "SELECT film_id FROM likes WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }

    @Override
    public void deleteFilm(Long id) {
        String insertQuery = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(insertQuery, id);
    }
}