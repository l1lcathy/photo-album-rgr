package com.photoalbum.repository.jdbc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
        String sql = "SELECT * FROM photos WHERE album_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, photoRowMapper, albumId);
    }
    
    public List<Photo> findByUserId(Long userId) {
        String sql = "SELECT * FROM photos WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, photoRowMapper, userId);
    }
    
    public Optional<Photo> findById(Long id) {
        String sql = "SELECT * FROM photos WHERE id = ?";
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
        String sql = "SELECT p.* FROM photos p " +
                     "JOIN photo_tags pt ON p.id = pt.photo_id " +
                     "WHERE pt.tag_id = ? ORDER BY p.created_at DESC";
        return jdbcTemplate.query(sql, photoRowMapper, tagId);
    }
}