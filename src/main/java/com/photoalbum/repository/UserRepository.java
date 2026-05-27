package com.photoalbum.repository;

import java.util.Optional;

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

    public Optional<User> findByUsername(String username) {

        String sql = """
            SELECT * FROM users
            WHERE username = ?
            """;

        try {

            User user = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> {

                    User u = new User();

                    u.setId(rs.getLong("id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setPassword(rs.getString("password_hash"));
                    u.setRole(rs.getString("role"));
                    u.setEnabled(rs.getBoolean("enabled"));

                    return u;
                },
                username
            );

            return Optional.ofNullable(user);

        } catch (Exception e) {
            return Optional.empty();
        }
    }
}