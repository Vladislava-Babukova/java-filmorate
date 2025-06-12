package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    User create(User user);

    User update(User user);

    List<User> getAllUsers();

    User getUser(Long id);

    User addFriend(Long userId, Long friendId);

    Set<User> getFriends(Long userId);

    User deleteFriend(Long userId, Long friendId);

    Set<User> getCommonFriends(Long userId, Long friendId);

    List<Long> findUsersWithSimilarTastes(Long userId);

    List<Long> getFilmsLikedByUser(Long userId);
}