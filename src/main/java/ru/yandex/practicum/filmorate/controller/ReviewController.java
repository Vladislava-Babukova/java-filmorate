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
        return reviewsService.getReviewById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable Long id) {
        reviewsService.delete(id);
    }

    @PutMapping("{id}/like/{userId}")
    public Review like(@PathVariable Long id, @PathVariable Long userId) {
        return reviewsService.like(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public Review dislike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewsService.dislike(id, userId);
    }

    @GetMapping()
    public List<Review> getReviews(@RequestParam(required = false) Long filmId,
                                   @RequestParam(required = false, defaultValue = "10") Long count) {
        return reviewsService.getReviews(filmId, count);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Review deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewsService.deleteLikeByReview(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public Review deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        return reviewsService.deleteDislikeByReview(id, userId);
    }

    @GetMapping("/grade/{id}")
    public List<GradeReview> getGradesByReview(@PathVariable Long id) {
        return reviewsService.getGradesByReview(id);
    }
}
