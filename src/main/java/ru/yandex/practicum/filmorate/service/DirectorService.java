package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dbStorage.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage storage;

    public Director create(Director director) {
        log.info("Начато создание Режисёра.Получен объект{}", director.getId());
        if (director == null) {
            throw new DataNotFoundException("director не может быть null");
        }
        return storage.create(director);
    }

    public Director getDirector(Long id) {
        return storage.getDirector(id);
    }

    public List<Director> getAll() {
        return storage.getAll();
    }

    public Director update(Director director) {
        log.info("Начато обновление режисёра.Получен объект{}", director.getId());
        if (director == null) {
            throw new ValidationException("director не должен быть null");
        }
        return storage.update(director);
    }

    public Director delete(Long id) {
        return storage.delete(id);
    }

}
