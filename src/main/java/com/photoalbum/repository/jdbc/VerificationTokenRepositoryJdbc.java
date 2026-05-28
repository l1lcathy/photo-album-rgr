package com.photoalbum.repository.jdbc;

import com.photoalbum.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class VerificationTokenRepositoryJdbc {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<VerificationToken> rowMapper = (rs, rowNum) -> {
        VerificationToken token = new VerificationToken();
        token.setId(rs.getLong("id"));
        token.setUserId(rs.getLong("user_id"));
        token.setToken(rs.getString("token"));
        token.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());
        return token;
    };

    public void save(VerificationToken token) {
        String sql = "INSERT INTO verification_tokens (user_id, token, expiry_date) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, token.getUserId(), token.getToken(), 
                           Timestamp.valueOf(token.getExpiryDate()));
    }

    public Optional<VerificationToken> findByToken(String token) {
        String sql = "SELECT * FROM verification_tokens WHERE token = ?";
        try {
            VerificationToken vt = jdbcTemplate.queryForObject(sql, rowMapper, token);
            return Optional.ofNullable(vt);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void deleteByUserId(Long userId) {
        String sql = "DELETE FROM verification_tokens WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}