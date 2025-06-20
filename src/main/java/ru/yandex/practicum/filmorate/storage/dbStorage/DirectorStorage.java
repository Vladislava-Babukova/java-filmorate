package ru.yandex.practicum.filmorate.storage.dbStorage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    Director create(Director director);

    Director getDirector(Long id);

    List<Director> getAll();

    Director update(Director director);

    Director delete(Long id);

}
