package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dbStorage.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage storage;

    public Director create(Director director) {
        return storage.create(director);
    }

    public Director getDirector(Long id) {
        return storage.getDirector(id);
    }

    public List<Director> getAll() {
        return storage.getAll();
    }

    public Director update(Director director) {
        if (director == null) {
            throw new ValidationException("director не должен быть null");
        }
        return storage.update(director);
    }

    public Director delete(Long id) {
        return storage.delete(id);
    }
}
