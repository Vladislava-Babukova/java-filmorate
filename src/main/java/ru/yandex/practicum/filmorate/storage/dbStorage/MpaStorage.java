package ru.yandex.practicum.filmorate.storage.dbStorage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    public List<Mpa> getAllMpa();

    public Mpa getMpa(Long id);
}
