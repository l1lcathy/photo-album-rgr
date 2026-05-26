package com.photoalbum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.photoalbum.model.User;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(User user) {

        String sql = """
            INSERT INTO users(username, email, password_hash, role, enabled)
            VALUES (?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(
            sql,
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            "USER",
            true
        );
    }
}