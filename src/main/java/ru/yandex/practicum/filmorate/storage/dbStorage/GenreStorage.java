package ru.yandex.practicum.filmorate.storage.dbStorage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    public List<Genre> getAllGenres();

    public Genre getGenre(Long genre_id);
}
