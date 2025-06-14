package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.GradeReview;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewsService;

    @Autowired
    public ReviewController(ReviewService reviewsService) {
        this.reviewsService = reviewsService;
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info("инициирован запрос на создание отзыва: {}", review);
        return reviewsService.crete(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("инициирован запрос на обновление отзыва: {}", review);
        return reviewsService.update(review);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        log.info("инициирован запрос на получение отзыва по id: {}", id);
        return reviewsService.getReviewById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable Long id) {
        log.info("инициирован запрос на удаление отзыва id: {}", id);
        reviewsService.delete(id);
    }

    @PutMapping("{id}/like/{userId}")
    public Review like(@PathVariable Long id, @PathVariable Long userId) {
        log.info("инициирован запрос на добавление лайка отзыву с id {} пользователем c id {}", id, userId);
        return reviewsService.like(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public Review dislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("инициирован запрос на добавление дизлайка отзыву с id {} пользователем c id {}", id, userId);
        return reviewsService.dislike(id, userId);
    }

    @GetMapping()
    public List<Review> getReviews(@RequestParam(required = false) Long filmId,
                                   @RequestParam(required = false, defaultValue = "10") Long count) {
        log.info("инициирован запрос на получение отзывов для фильма {}, количество записей {}", filmId, count);
        return reviewsService.getReviews(filmId, count);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Review deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("инициирован запрос на удаление лайка отзыву с id {} пользователем c id {}", id, userId);
        return reviewsService.deleteLikeByReview(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public Review deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("инициирован запрос на удаление дизлайка отзыву с id {} пользователем c id {}", id, userId);
        return reviewsService.deleteDislikeByReview(id, userId);
    }

    @GetMapping("/grade/{id}")
    public List<GradeReview> getGradesByReview(@PathVariable Long id) {
        log.info("инициирован запрос на получение оценок по отзыву с id {}", id);
        return reviewsService.getGradesByReview(id);
    }
}
