package com.photoalbum.repository.jdbc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.photoalbum.model.Photo;

@Repository
public class PhotoRepositoryJdbc {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PhotoRowMapper photoRowMapper;
    
    public List<Photo> findByAlbumId(Long albumId) {
        String sql = "SELECT p.*, u.username FROM photos p " +
                     "JOIN users u ON p.user_id = u.id " +
                     "WHERE p.album_id = ? ORDER BY p.created_at DESC";
        return jdbcTemplate.query(sql, photoRowMapper, albumId);
    }
    
    public List<Photo> findAll() {
        String sql = "SELECT p.*, u.username FROM photos p JOIN users u ON p.user_id = u.id ORDER BY p.created_at DESC";
        return jdbcTemplate.query(sql, photoRowMapper);
    }
    
    public List<Photo> findByUserId(Long userId) {
        String sql = "SELECT p.*, u.username FROM photos p " +
                     "JOIN users u ON p.user_id = u.id " +
                     "WHERE p.user_id = ? ORDER BY p.created_at DESC";
        return jdbcTemplate.query(sql, photoRowMapper, userId);
    }
    
    public Optional<Photo> findById(Long id) {
        String sql = "SELECT p.*, u.username FROM photos p " +
                     "JOIN users u ON p.user_id = u.id " +
                     "WHERE p.id = ?";
        try {
            Photo photo = jdbcTemplate.queryForObject(sql, photoRowMapper, id);
            return Optional.ofNullable(photo);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public Photo save(Photo photo) {
        String sql = "INSERT INTO photos (title, description, image_path, album_id, user_id, rating, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        Long generatedId = jdbcTemplate.queryForObject(sql, Long.class,
            photo.getTitle(),
            photo.getDescription(),
            photo.getImagePath(),
            photo.getAlbumId(),
            photo.getUserId(),
            photo.getRating() != null ? photo.getRating() : 0,
            Timestamp.valueOf(photo.getCreatedAt() != null ? photo.getCreatedAt() : LocalDateTime.now())
        );
        
        photo.setId(generatedId);
        return photo;
    }
    
    public int update(Photo photo) {
        String sql = "UPDATE photos SET title = ?, description = ?, rating = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
            photo.getTitle(),
            photo.getDescription(),
            photo.getRating(),
            photo.getId()
        );
    }
    
    public int deleteById(Long id) {
        String sql = "DELETE FROM photos WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
    
    public List<Photo> findByTagId(Long tagId) {
        String sql = "SELECT p.*, u.username FROM photos p " +
                     "JOIN photo_tags pt ON p.id = pt.photo_id " +
                     "JOIN users u ON p.user_id = u.id " +
                     "WHERE pt.tag_id = ? ORDER BY p.created_at DESC";
        return jdbcTemplate.query(sql, photoRowMapper, tagId);
    }
    
    public List<Photo> findByUsername(String username) {
        String sql = "SELECT p.*, u.username FROM photos p " +
                     "JOIN users u ON p.user_id = u.id " +
                     "WHERE LOWER(u.username) LIKE LOWER(?) ORDER BY p.created_at DESC";
        return jdbcTemplate.query(sql, photoRowMapper, "%" + username + "%");
    }
    
    public List<Photo> findByDateRange(LocalDateTime from, LocalDateTime to) {
        String sql = "SELECT p.*, u.username FROM photos p " +
                     "JOIN users u ON p.user_id = u.id WHERE 1=1";
        List<Object> params = new ArrayList<>();
        
        if (from != null) {
            sql += " AND p.created_at >= ?";
            params.add(Timestamp.valueOf(from));
        }
        if (to != null) {
            sql += " AND p.created_at <= ?";
            params.add(Timestamp.valueOf(to));
        }
        sql += " ORDER BY p.created_at DESC";
        
        return jdbcTemplate.query(sql, photoRowMapper, params.toArray());
    }
}