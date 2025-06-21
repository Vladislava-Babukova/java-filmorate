package ru.yandex.practicum.filmorate.storage.dbStorage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    Review getReviewById(Long id);

    void delete(Long id);

    List<Review> getAllReviews(Long count);

    List<Review> getReviewsByFilm(Long filmId, Long count);

}
