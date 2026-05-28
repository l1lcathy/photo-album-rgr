package com.photoalbum.repository.jdbc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.photoalbum.model.Album;
import com.photoalbum.model.AccessLevel;

@Repository
public class AlbumRepositoryJdbc {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private AlbumRowMapper albumRowMapper;
    
    public List<Album> findByOwnerId(Long ownerId) {
        String sql = "SELECT a.*, u.username FROM albums a " +
                     "JOIN users u ON a.user_id = u.id " +
                     "WHERE a.user_id = ? ORDER BY a.created_at DESC";
        return jdbcTemplate.query(sql, albumRowMapper, ownerId);
    }
    
    public Optional<Album> findById(Long id) {
        String sql = "SELECT a.*, u.username FROM albums a " +
                     "JOIN users u ON a.user_id = u.id " +
                     "WHERE a.id = ?";
        try {
            Album album = jdbcTemplate.queryForObject(sql, albumRowMapper, id);
            return Optional.ofNullable(album);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public List<Album> findPublicAlbums() {
        String sql = "SELECT a.*, u.username FROM albums a " +
                     "JOIN users u ON a.user_id = u.id " +
                     "WHERE a.access_type = 'PUBLIC' ORDER BY a.created_at DESC";
        return jdbcTemplate.query(sql, albumRowMapper);
    }
    
    // НОВЫЙ МЕТОД - найти доступные альбомы для пользователя
    public List<Album> findAccessibleAlbums(Long userId, List<Long> friendsIds) {
        String sql = "SELECT a.*, u.username FROM albums a " +
                     "JOIN users u ON a.user_id = u.id " +
                     "WHERE a.user_id = ? OR a.access_type = 'PUBLIC'";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        
        if (friendsIds != null && !friendsIds.isEmpty()) {
            String placeholders = friendsIds.stream().map(id -> "?").collect(Collectors.joining(","));
            sql += " OR (a.user_id IN (" + placeholders + ") AND a.access_type = 'FRIENDS')";
            params.addAll(friendsIds);
        }
        sql += " ORDER BY a.created_at DESC";
        
        return jdbcTemplate.query(sql, albumRowMapper, params.toArray());
    }
    
    // НОВЫЙ МЕТОД - проверить доступ к альбому
    public boolean hasAccess(Long albumId, Long userId, List<Long> friendsIds) {
        String sql = "SELECT a.access_type, a.user_id FROM albums a WHERE a.id = ?";
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, albumId);
            String accessType = (String) result.get("access_type");
            Long ownerId = (Long) result.get("user_id");
            
            // Свой альбом
            if (ownerId.equals(userId)) {
                return true;
            }
            // Публичный
            if ("PUBLIC".equals(accessType)) {
                return true;
            }
            // Для друзей
            if ("FRIENDS".equals(accessType) && friendsIds != null && friendsIds.contains(ownerId)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public Album save(Album album) {
        String sql = "INSERT INTO albums (user_id, title, description, access_type, created_at) " +
                     "VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        Long generatedId = jdbcTemplate.queryForObject(sql, Long.class,
            album.getOwnerId(),
            album.getName(),
            album.getDescription(),
            album.getAccessLevel() != null ? album.getAccessLevel().name() : "PUBLIC",
            Timestamp.valueOf(album.getCreatedAt() != null ? album.getCreatedAt() : LocalDateTime.now())
        );
        
        album.setId(generatedId);
        return album;
    }
    
    public int update(Album album) {
        String sql = "UPDATE albums SET title = ?, description = ?, access_type = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
            album.getName(),
            album.getDescription(),
            album.getAccessLevel() != null ? album.getAccessLevel().name() : "PUBLIC",
            album.getId()
        );
    }
    
    public int deleteById(Long id) {
        String sql = "DELETE FROM albums WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
    
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM albums WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}