package com.photoalbum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class VerificationTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    public VerificationTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Long userId, String token, LocalDateTime expiryDate) {
        jdbcTemplate.update(
                "INSERT INTO verification_tokens (user_id, token, expiry_date) VALUES (?, ?, ?)",
                userId,
                token,
                expiryDate
        );
    }

    public Optional<Long> findUserIdByToken(String token) {
        return jdbcTemplate.query(
                "SELECT user_id FROM verification_tokens WHERE token = ?",
                rs -> rs.next() ? Optional.of(rs.getLong("user_id")) : Optional.empty(),
                token
        );
    }

    public void deleteByToken(String token) {
        jdbcTemplate.update("DELETE FROM verification_tokens WHERE token = ?", token);
    }
}