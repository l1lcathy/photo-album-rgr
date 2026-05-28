package com.photoalbum.repository.jdbc;

import com.photoalbum.model.Friendship;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendshipRowMapper implements RowMapper<Friendship> {
    
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setId(rs.getLong("id"));
        friendship.setUserId(rs.getLong("user_id"));
        friendship.setFriendId(rs.getLong("friend_id"));
        friendship.setStatus(rs.getString("status"));
        
        java.sql.Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            friendship.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        return friendship;
    }
}