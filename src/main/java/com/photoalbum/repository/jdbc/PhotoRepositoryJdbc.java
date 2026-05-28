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

import com.photoalbum.model.Photo;

@Repository
public class PhotoRepositoryJdbc {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PhotoRowMapper photoRowMapper;
    
    // Найти все фото в альбоме
    public List<Photo> findByAlbumId(Long albumId) {
        String sql = "SELECT * FROM photos WHERE album_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, photoRowMapper, albumId);
    }
    
    // Найти все фото пользователя
    public List<Photo> findByUserId(Long userId) {
        String sql = "SELECT * FROM photos WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, photoRowMapper, userId);
    }
    
    // Найти фото по ID
    public Optional<Photo> findById(Long id) {
        String sql = "SELECT * FROM photos WHERE id = ?";
        try {
            Photo photo = jdbcTemplate.queryForObject(sql, photoRowMapper, id);
            return Optional.ofNullable(photo);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    // Сохранить новое фото
    public Photo save(Photo photo) {
        String sql = "INSERT INTO photos (title, description, image_path, album_id, user_id, rating, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, photo.getTitle());
            ps.setString(2, photo.getDescription());
            ps.setString(3, photo.getImagePath());
            ps.setLong(4, photo.getAlbumId());
            ps.setLong(5, photo.getUserId());
            ps.setInt(6, photo.getRating() != null ? photo.getRating() : 0);
            ps.setTimestamp(7, Timestamp.valueOf(photo.getCreatedAt() != null ? 
                           photo.getCreatedAt() : LocalDateTime.now()));
            return ps;
        }, keyHolder);
        
        if (keyHolder.getKey() != null) {
            photo.setId(keyHolder.getKey().longValue());
        }
        return photo;
    }
    
    // Обновить фото (название, описание, рейтинг)
    public int update(Photo photo) {
        String sql = "UPDATE photos SET title = ?, description = ?, rating = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
            photo.getTitle(),
            photo.getDescription(),
            photo.getRating(),
            photo.getId()
        );
    }
    
    // Удалить фото
    public int deleteById(Long id) {
        String sql = "DELETE FROM photos WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
    
    // Найти все фото по тегу (через связующую таблицу)
    public List<Photo> findByTagId(Long tagId) {
        String sql = "SELECT p.* FROM photos p " +
                     "JOIN photo_tags pt ON p.id = pt.photo_id " +
                     "WHERE pt.tag_id = ? ORDER BY p.created_at DESC";
        return jdbcTemplate.query(sql, photoRowMapper, tagId);
    }
}