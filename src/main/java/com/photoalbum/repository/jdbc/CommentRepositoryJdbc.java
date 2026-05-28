package com.photoalbum.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.photoalbum.model.Comment;

@Repository
public class CommentRepositoryJdbc {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private CommentRowMapper commentRowMapper;
    
    // Найти все комментарии к фото
    public List<Comment> findByPhotoId(Long photoId) {
        String sql = "SELECT * FROM comments WHERE photo_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, commentRowMapper, photoId);
    }
    
    // Найти комментарий по ID
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        try {
            Comment comment = jdbcTemplate.queryForObject(sql, commentRowMapper, id);
            return Optional.ofNullable(comment);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    // Сохранить новый комментарий
    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments (photo_id, user_id, text, created_at) " +
                     "VALUES (?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, comment.getPhotoId());
            ps.setLong(2, comment.getUserId());
            ps.setString(3, comment.getText());
            ps.setTimestamp(4, Timestamp.valueOf(comment.getCreatedAt() != null ? 
                           comment.getCreatedAt() : LocalDateTime.now()));
            return ps;
        }, keyHolder);
        
        if (keyHolder.getKey() != null) {
            comment.setId(keyHolder.getKey().longValue());
        }
        return comment;
    }
    
    // Удалить комментарий
    public int deleteById(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
    
    // Удалить все комментарии к фото
    public int deleteByPhotoId(Long photoId) {
        String sql = "DELETE FROM comments WHERE photo_id = ?";
        return jdbcTemplate.update(sql, photoId);
    }
    
    // Количество комментариев у фото
    public int countByPhotoId(Long photoId) {
        String sql = "SELECT COUNT(*) FROM comments WHERE photo_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, photoId);
        return count != null ? count : 0;
    }
}