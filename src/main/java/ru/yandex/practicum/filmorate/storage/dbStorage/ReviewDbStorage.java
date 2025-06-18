package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dbStorage.mapping.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ReviewDbStorage implements ReviewStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ReviewRowMapper reviewRowMapper;

    @Override
    public Review create(Review review) {
        String query = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            stmt.setLong(5, review.getUseful());
            return stmt;
        }, keyHolder);

        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;

        if (id == null) {
            throw new RuntimeException("Не удалось получить ID отзыва после вставки");
        }
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review update(Review review) {
        if (getReviewById(review.getReviewId()) == null) {
            throw new DataNotFoundException("отзыв с ID " + review.getReviewId() + " не найден");
        }

        String query = "UPDATE reviews SET " +
                "content = ?," +
                "is_positive = ?," +
                "useful = ?" +
                "WHERE review_id = ?";
        jdbcTemplate.update(query,
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public Review getReviewById(Long id) {
        try {
            String query = "SELECT * FROM reviews WHERE review_id = ?";
            return jdbcTemplate.queryForObject(query, reviewRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("отзыв с ID " + id + " не найден");
        }
    }

    @Override
    public void delete(Long id) {
        if (getReviewById(id) == null) {
            throw new DataNotFoundException("отзыв с ID " + id + " не найден");
        }
        String query = "DELETE FROM reviews WHERE review_id = ?;";
        jdbcTemplate.update(query, id);
    }

    @Override
    public List<Review> getAllReviews(Long count) {
        String query = "SELECT * FROM reviews LIMIT ?;";
        return jdbcTemplate.query(query, reviewRowMapper, count);
    }

    @Override
    public List<Review> getReviewsByFilm(Long filmId, Long count) {
        String query = "SELECT * FROM reviews WHERE film_id = ? LIMIT ?";
        return jdbcTemplate.query(query, reviewRowMapper, filmId, count);
    }
}
