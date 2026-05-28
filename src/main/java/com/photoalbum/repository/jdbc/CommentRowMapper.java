package com.photoalbum.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.photoalbum.model.Comment;

@Component
public class CommentRowMapper implements RowMapper<Comment> {
    
    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setPhotoId(rs.getLong("photo_id"));
        comment.setUserId(rs.getLong("user_id"));
        comment.setText(rs.getString("text"));
        
        java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            comment.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        return comment;
    }
}