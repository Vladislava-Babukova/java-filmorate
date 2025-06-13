package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Grade;
import ru.yandex.practicum.filmorate.model.GradeReview;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dbStorage.mapping.GradesReviewsRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class GradesReviewsDbStorage implements GradesReviewsStorage {
    private final ReviewStorage reviewStorage;
    private final JdbcTemplate jdbcTemplate;
    private final GradesReviewsRowMapper gradesReviewsRowMapper;

    @Autowired
    public GradesReviewsDbStorage(ReviewStorage reviewStorage, JdbcTemplate jdbcTemplate, GradesReviewsRowMapper gradesReviewsRowMapper) {
        this.reviewStorage = reviewStorage;
        this.jdbcTemplate = jdbcTemplate;
        this.gradesReviewsRowMapper = gradesReviewsRowMapper;
    }

    public Review addGrade(Long id, Long userId, boolean useful) {
        Review review = reviewStorage.getReviewById(id);
        String query = "UPDATE reviews SET " +
                "useful = ?" +
                "WHERE review_id = ?";
        String grade;
        if (review == null) {
            throw new DataNotFoundException("отзыв с ID " + id + " не найден");
        }
        if (useful) {
            grade = String.valueOf(Grade.LIKE);
            jdbcTemplate.update(query, review.getUseful() + 1, id);
        } else {
            grade = String.valueOf(Grade.DISLIKE);
            jdbcTemplate.update(query, review.getUseful() - 1, id);
        }
        String queryInsert = "INSERT INTO grades_reviews (user_id, grade, review_id) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, userId);
            stmt.setString(2, grade);
            stmt.setLong(3, id);
            return stmt;
        }, keyHolder);

        Long idGrade = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;

        if (idGrade == null) {
            throw new RuntimeException("Не удалось получить ID отзыва после вставки");
        }
        return reviewStorage.getReviewById(id);
    }

    @Override
    public List<GradeReview> getGradesByReview(Long id) {
        String query = "SELECT * FROM grades_reviews WHERE review_id = ?";
        return jdbcTemplate.query(query, gradesReviewsRowMapper, id);
    }

    @Override
    public Review deleteLike(Long id, Long userId) {
        String query = "DELETE FROM grades_reviews WHERE user_id = ? AND grade = 'LIKE' AND review_id = ?;";
        String queryUpdateReview = "UPDATE reviews SET useful = ? WHERE review_id = ?;";
        jdbcTemplate.update(query, userId, id);
        jdbcTemplate.update(queryUpdateReview, reviewStorage.getReviewById(id).getUseful() - 1, id);
        return reviewStorage.getReviewById(id);
    }

    @Override
    public Review deleteDislike(Long id, Long userId) {
        String query = "DELETE FROM grades_reviews WHERE user_id = ? AND grade = 'DISLIKE' AND review_id = ?;";
        String queryUpdateReview = "UPDATE reviews SET useful = ? WHERE review_id = ?;";
        jdbcTemplate.update(query, userId, id);
        jdbcTemplate.update(queryUpdateReview, reviewStorage.getReviewById(id).getUseful() + 1, id);
        return reviewStorage.getReviewById(id);
    }
}
