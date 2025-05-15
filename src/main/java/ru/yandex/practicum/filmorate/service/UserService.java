package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private long generateId = 0;
    private LocalDate dateNow = LocalDate.now();
    private final InMemoryUserStorage storage;

    public void checkBirthday(User user) {
        if (user.getBirthday().isAfter(dateNow)) {
            throw new ValidationException("Дата рождения некорректна");
        }
    }

    public void nameCreate(User user) {
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
    }

    public User create(User user) {
        checkBirthday(user);
        nameCreate(user);
        user.setId(++generateId);
        return storage.create(user);
    }

    public User update(User user) {
        checkBirthday(user);
        nameCreate(user);
        return storage.update(user);

    }

    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public User addFriend(Long id, Long friendId) {
        User user = storage.getUser(id);
        User friend = storage.getUser(friendId);
        if (user == null || friend == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        Set<Long> list = user.getFrendSet();
        Set<Long> listFriend = friend.getFrendSet();
        list.add(friendId);
        listFriend.add(id);
        user.setFrendSet(list);
        friend.setFrendSet(listFriend);
        storage.update(user);
        storage.update(friend);
        return user;
    }

    public User deleteFriend(Long id, Long friendId) {
        User user = storage.getUser(id);
        User friend = storage.getUser(friendId);
        if (user == null || friend == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        Set<Long> list = user.getFrendSet();
        Set<Long> listFriend = friend.getFrendSet();
        list.remove(friendId);
        listFriend.remove(id);
        user.setFrendSet(list);
        friend.setFrendSet(listFriend);
        storage.update(user);
        storage.update(friend);
        return user;
    }

    public List<User> mutualFriends(Long id, Long otherId) {
        return storage.mutualFriends(id, otherId);
    }

    public List<User> getUsersFriends(Long id) {
        return storage.getUsersFriends(id);
    }


    public User getUser(Long userId) {
        return storage.getUser(userId);
    }
}
