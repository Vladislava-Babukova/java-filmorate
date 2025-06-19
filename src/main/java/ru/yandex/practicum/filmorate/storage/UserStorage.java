package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public User create(User user);

    public User update(User user);

    public List<User> getAllUsers();

    public User getUser(Long id);

    public List<User> mutualFriends(Long id, Long otherId);

    public List<User> getUsersFriends(Long id);

}
