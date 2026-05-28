package com.photoalbum.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.photoalbum.model.Photo;

@Component
public class PhotoRowMapper implements RowMapper<Photo> {
    
    @Override
    public Photo mapRow(ResultSet rs, int rowNum) throws SQLException {
        Photo photo = new Photo();
        photo.setId(rs.getLong("id"));
        photo.setTitle(rs.getString("title"));
        photo.setDescription(rs.getString("description"));
        photo.setImagePath(rs.getString("image_path"));
        photo.setAlbumId(rs.getLong("album_id"));
        photo.setUserId(rs.getLong("user_id"));
        photo.setRating(rs.getInt("rating"));
        
        java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            photo.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        return photo;
    }
}