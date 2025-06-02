package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    public User create(User user);

    public User update(User user);

    public List<User> getAllUsers();

    public User getUser(Long id);

    public User addFriend(Long userId, Long friendId);

    public Set<User> getFriends(Long user_id);

    public User deleteFriend(Long userId, Long friendId);

    public Set<User> getCommonFriends(Long userId, Long friendId);

}
