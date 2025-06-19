package ru.yandex.practicum.filmorate.storage.dbStorage.mapping;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Grade;
import ru.yandex.practicum.filmorate.model.GradeReview;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GradesReviewsRowMapper implements RowMapper<GradeReview> {
    @Override
    public GradeReview mapRow(ResultSet rs, int rowNum) throws SQLException {
        GradeReview gradeReview = new GradeReview();
        gradeReview.setUserId(rs.getLong("user_id"));
        gradeReview.setGrade(Grade.valueOf(rs.getString("grade")));
        gradeReview.setReviewId(rs.getLong("review_id"));
        return gradeReview;
    }
}
