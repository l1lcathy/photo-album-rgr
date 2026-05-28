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

import com.photoalbum.model.Album;
import com.photoalbum.model.AccessLevel;

@Repository
public class AlbumRepositoryJdbc {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private AlbumRowMapper albumRowMapper;
    
    // Найти все альбомы пользователя
    public List<Album> findByOwnerId(Long ownerId) {
        String sql = "SELECT * FROM albums WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, albumRowMapper, ownerId);
    }
    
    // Найти альбом по ID
    public Optional<Album> findById(Long id) {
        String sql = "SELECT * FROM albums WHERE id = ?";
        try {
            Album album = jdbcTemplate.queryForObject(sql, albumRowMapper, id);
            return Optional.ofNullable(album);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    // Найти все публичные альбомы (для галереи)
    public List<Album> findPublicAlbums() {
        String sql = "SELECT * FROM albums WHERE access_type = 'PUBLIC' ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, albumRowMapper);
    }
    
    // Сохранить новый альбом
    public Album save(Album album) {
        String sql = "INSERT INTO albums (user_id, title, description, access_type, created_at) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, album.getOwnerId());
            ps.setString(2, album.getName());
            ps.setString(3, album.getDescription());
            ps.setString(4, album.getAccessLevel() != null ? album.getAccessLevel().name() : "PUBLIC");
            ps.setTimestamp(5, Timestamp.valueOf(album.getCreatedAt() != null ? 
                           album.getCreatedAt() : LocalDateTime.now()));
            return ps;
        }, keyHolder);
        
        if (keyHolder.getKey() != null) {
            album.setId(keyHolder.getKey().longValue());
        }
        return album;
    }
    
    // Обновить альбом
    public int update(Album album) {
        String sql = "UPDATE albums SET title = ?, description = ?, access_type = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
            album.getName(),
            album.getDescription(),
            album.getAccessLevel() != null ? album.getAccessLevel().name() : "PUBLIC",
            album.getId()
        );
    }
    
    // Удалить альбом
    public int deleteById(Long id) {
        String sql = "DELETE FROM albums WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
    
    // Проверить, существует ли альбом
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM albums WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}