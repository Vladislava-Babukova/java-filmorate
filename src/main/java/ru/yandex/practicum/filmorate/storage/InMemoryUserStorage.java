package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistExeption;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> storageUser = new HashMap<>();

    public User create(User user) {
        if (exists(user)) {
            throw new DataAlreadyExistExeption("Данный пользователь уже существует");
        }
        storageUser.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        if (!exists(user)) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        storageUser.put(user.getId(), user);
        return user;
    }

    private boolean exists(User user) {
        if (storageUser.containsKey(user.getId())) {
            return true;
        } else {
            return false;
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<User>(storageUser.values());
    }

    public User getUser(Long id) {
        return storageUser.get(id);
    }

    public List<User> mutualFriends(Long id, Long otherId) {
        if (id != null || otherId != null) {
            Set<Long> friendIdList = storageUser.get(otherId).getFrendSet();
            Set<Long> myIdList = storageUser.get(id).getFrendSet();
            if (friendIdList != null || myIdList != null) {
                return friendIdList.stream()
                        .filter(myIdList::contains)
                        .map(storageUser::get)
                        .collect(Collectors.toUnmodifiableList());
            } else {
                throw new DataNotFoundException("Список друзей не найден");
            }
        } else {
            throw new DataNotFoundException("пользователь не найден");
        }
    }

    public List<User> getUsersFriends(Long id) {
        if (id != null) {
            if (storageUser.containsKey(id)) {
                Set<Long> friendIdList = storageUser.get(id).getFrendSet();
                return storageUser.values().stream()
                        .filter(user -> friendIdList.contains(user.getId()))
                        .collect(Collectors.toUnmodifiableList());
            } else {
                throw new DataNotFoundException("пользователь не найден");
            }
        } else {
            throw new DataNotFoundException("пользователь не найден");
        }
    }

}
