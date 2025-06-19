package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistExeption;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.GradesReviewsStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.ReviewStorage;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GradesReviewsStorage gradesReviewsStorage;
    private final EventService eventService;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, UserStorage userStorage, FilmStorage filmStorage, GradesReviewsStorage gradesReviewsStorage, EventService eventService) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.gradesReviewsStorage = gradesReviewsStorage;
        this.eventService = eventService;
    }

    public Review crete(@Valid Review review) {
        if (review.getUseful() == null) {
            review.setUseful(0L);
        }
        if (review.getUseful() != 0) {
            throw new ValidationException("при создании отзыва невозможно установить рейтинг отзыва не равный 0");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("не указан тип отзыва");
        }
        userStorage.getUser(review.getUserId());
        filmStorage.getFilm(review.getFilmId());
        Review result = reviewStorage.create(review);
        eventService.createEvent(Instant.now(), result.getUserId(), EventType.REVIEW, OperationType.ADD, result.getReviewId());
        return result;
    }

    public Review update(@Valid Review review) {
        reviewStorage.getReviewById(review.getReviewId());
        eventService.createEvent(Instant.now(), review.getUserId(), EventType.REVIEW, OperationType.UPDATE, review.getReviewId());
        return reviewStorage.update(review);
    }

    public Review getReviewById(long id) {
        return reviewStorage.getReviewById(id);
    }

    public void delete(Long id) {
        eventService.createEvent(Instant.now(), reviewStorage.getReviewById(id).getUserId(), EventType.REVIEW, OperationType.REMOVE, id);
        reviewStorage.delete(id);
    }

    public Review like(Long id, Long userId) {
        userStorage.getUser(userId);
        List<GradeReview> gradeReviews = gradesReviewsStorage.getGradesByReview(id);
        if (!gradeReviews.isEmpty()) {
            for (GradeReview gr : gradeReviews) {
                if (Objects.equals(gr.getUserId(), userId) && gr.getGrade() == Grade.LIKE) {
                    throw new DataAlreadyExistExeption("пользователь уже ставил оценку отзыву");
                }
            }
        }
        return gradesReviewsStorage.addGrade(id, userId, true);
    }

    public Review dislike(Long id, Long userId) {
        userStorage.getUser(userId);
        List<GradeReview> gradeReviews = gradesReviewsStorage.getGradesByReview(id);
        if (!gradeReviews.isEmpty()) {
            for (GradeReview gr : gradeReviews) {
                if (Objects.equals(gr.getUserId(), userId) && gr.getGrade() == Grade.DISLIKE) {
                    throw new DataAlreadyExistExeption("пользователь уже ставил оценку отзыву");
                }
            }
            for (GradeReview gr : gradeReviews) {
                if (Objects.equals(gr.getUserId(), userId) && gr.getGrade() == Grade.LIKE) {
                    deleteLikeByReview(id, userId);
                }
            }
        }
        return gradesReviewsStorage.addGrade(id, userId, false);
    }

    public List<Review> getReviews(Long filmId, Long count) {
        if (filmId != null) {
            filmStorage.getFilm(filmId);
            return reviewStorage.getReviewsByFilm(filmId, count);
        }
        return reviewStorage.getAllReviews(count);
    }

    public Review deleteLikeByReview(Long id, Long userId) {
        reviewStorage.getReviewById(id);
        userStorage.getUser(userId);
        return gradesReviewsStorage.deleteLike(id, userId);
    }

    public Review deleteDislikeByReview(Long id, Long userId) {
        reviewStorage.getReviewById(id);
        userStorage.getUser(userId);
        return gradesReviewsStorage.deleteDislike(id, userId);
    }

    public List<GradeReview> getGradesByReview(Long id) {
        return gradesReviewsStorage.getGradesByReview(id);
    }
}
