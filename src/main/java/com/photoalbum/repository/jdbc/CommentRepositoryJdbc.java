package com.photoalbum.repository.jdbc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.photoalbum.model.Comment;

@Repository
public class CommentRepositoryJdbc {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private CommentRowMapper commentRowMapper;
    
    public List<Comment> findByPhotoId(Long photoId) {
        String sql = "SELECT c.*, u.username FROM comments c JOIN users u ON c.user_id = u.id WHERE c.photo_id = ? ORDER BY c.created_at DESC";
        return jdbcTemplate.query(sql, commentRowMapper, photoId);
    }
    
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT c.*, u.username FROM comments c JOIN users u ON c.user_id = u.id WHERE c.id = ?";
        try {
            Comment comment = jdbcTemplate.queryForObject(sql, commentRowMapper, id);
            return Optional.ofNullable(comment);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments (photo_id, user_id, text, created_at) VALUES (?, ?, ?, ?) RETURNING id";
        
        Long generatedId = jdbcTemplate.queryForObject(sql, Long.class,
            comment.getPhotoId(),
            comment.getUserId(),
            comment.getText(),
            Timestamp.valueOf(comment.getCreatedAt() != null ? comment.getCreatedAt() : LocalDateTime.now())
        );
        
        comment.setId(generatedId);
        return comment;
    }
    
    public int deleteById(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
    
    public int deleteByPhotoId(Long photoId) {
        String sql = "DELETE FROM comments WHERE photo_id = ?";
        return jdbcTemplate.update(sql, photoId);
    }
    
    public int countByPhotoId(Long photoId) {
        String sql = "SELECT COUNT(*) FROM comments WHERE photo_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, photoId);
        return count != null ? count : 0;
    }
}