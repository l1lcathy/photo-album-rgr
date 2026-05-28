package com.photoalbum.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.photoalbum.model.Album;
import com.photoalbum.model.AccessLevel;

@Component
public class AlbumRowMapper implements RowMapper<Album> {
    
    @Override
    public Album mapRow(ResultSet rs, int rowNum) throws SQLException {
        Album album = new Album();
        album.setId(rs.getLong("id"));
        album.setOwnerId(rs.getLong("user_id"));
        album.setName(rs.getString("title"));
        album.setDescription(rs.getString("description"));
        
        // Преобразуем строку из БД в enum AccessLevel
        String accessType = rs.getString("access_type");
        if (accessType != null) {
            album.setAccessLevel(AccessLevel.valueOf(accessType));
        }
        
        java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            album.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        return album;
    }
}