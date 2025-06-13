package ru.yandex.practicum.filmorate.storage.dbStorage;

import ru.yandex.practicum.filmorate.model.GradeReview;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface GradesReviewsStorage {

    Review addGrade(Long id, Long userId, boolean useful);

    Review deleteLike(Long id, Long userId);

    Review deleteDislike(Long id, Long userId);

    List<GradeReview> getGradesByReview(Long id);
}
