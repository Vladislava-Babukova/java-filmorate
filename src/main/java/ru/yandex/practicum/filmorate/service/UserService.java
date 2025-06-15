package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.EventStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.UserDbStorage;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage; // <--- тк ниже появляется экз-ы FilmStorage и UserDbStorage, пришлось
    private final FilmStorage filmStorage; // поменять имя переменной storage на userStorage, чтобы отличать их (простите)
    private final UserDbStorage userDbStorage;
    private final EventStorage eventStorage;
    private final EventService eventService;
    private long generateId = 0;
    private final LocalDate dateNow = LocalDate.now();

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
        return userStorage.create(user);
    }

    public User update(User user) {
        checkBirthday(user);
        nameCreate(user);
        return userStorage.update(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addFriend(Long id, Long friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        if (user == null || friend == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        eventService.createEvent(OffsetDateTime.now(), id, EventType.FRIEND, OperationType.ADD,friendId);
        return userStorage.addFriend(id, friendId);
    }

    public Set<User> getFriends(Long userId) {

        return userStorage.getFriends(userId);
    }

    public User deleteFriend(Long id, Long friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        if (user == null || friend == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        user = userStorage.deleteFriend(id, friendId);
        return user;
    }

    public Set<User> mutualFriends(Long id, Long otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

    public User getUser(Long userId) {
        return userStorage.getUser(userId);
    }

    public List<Film> getRecommendations(Long userId) {
        // 1. Проверяем существование пользователя
        if (!userDbStorage.checkId(userId)) {
            throw new DataNotFoundException("Пользователь с ID " + userId + " не найден");
        }

        // 2. Находим пользователей с похожими вкусами
        List<Long> similarUsers = userStorage.findUsersWithSimilarTastes(userId);
        if (similarUsers.isEmpty()) {
            return Collections.emptyList();
        }
        Long mostSimilarUserId = similarUsers.getFirst();

        // 3. Получаем фильмы, которые лайкнул похожий пользователь
        List<Long> similarUserLikedFilms = userStorage.getFilmsLikedByUser(mostSimilarUserId);

        // 4. Получаем фильмы, которые лайкнул текущий пользователь
        List<Long> currentUserLikedFilms = userStorage.getFilmsLikedByUser(userId);

        // 5. Исключаем уже просмотренные фильмы
        List<Long> recommendedFilmIds = similarUserLikedFilms.stream()
                .filter(filmId -> !currentUserLikedFilms.contains(filmId))
                .toList();

        // 6. Получаем полные данные о фильмах
        return recommendedFilmIds.stream()
                .map(filmStorage::getFilm)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        userStorage.deleteFilm(id);
    }

    public List<Event> getFeedForUser(Long userId) {

        return eventStorage.getFeedForUser(userId);
    }
}