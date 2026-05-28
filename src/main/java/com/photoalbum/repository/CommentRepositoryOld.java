package com.photoalbum.repository;

import com.photoalbum.model.Comment;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CommentRepositoryOld {

    private final JdbcTemplate jdbcTemplate;

    public CommentRepositoryOld(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Comment save(Comment comment) {
        String sql = """
                INSERT INTO comments (photo_id, user_id, text)
                VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, comment.getPhotoId());
            ps.setLong(2, comment.getUserId());
            ps.setString(3, comment.getText());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            comment.setId(keyHolder.getKey().longValue());
        }
        return comment;
    }

    public List<Comment> findByPhotoId(Long photoId) {
        return jdbcTemplate.query(
                "SELECT * FROM comments WHERE photo_id = ? ORDER BY created_at DESC",
                new BeanPropertyRowMapper<>(Comment.class),
                photoId
        );
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM comments WHERE id = ?", id);
    }
}