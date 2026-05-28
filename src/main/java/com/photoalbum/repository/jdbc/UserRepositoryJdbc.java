package com.photoalbum.repository.jdbc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.photoalbum.model.User;

@Repository
public class UserRepositoryJdbc {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private UserRowMapper userRowMapper;
    
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id";
        return jdbcTemplate.query(sql, userRowMapper);
    }
    
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, email);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    // ИСПРАВЛЕННЫЙ МЕТОД SAVE - больше нет ошибки с getKey
    public User save(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, role, enabled, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        
        Long generatedId = jdbcTemplate.queryForObject(sql, Long.class,
            user.getUsername(),
            user.getEmail(),
            user.getPasswordHash(),
            user.getRole(),
            user.isEnabled(),
            Timestamp.valueOf(user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now())
        );
        
        user.setId(generatedId);
        return user;
    }
    
    public int update(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, password_hash = ?, " +
                     "role = ?, enabled = ? WHERE id = ?";
        return jdbcTemplate.update(sql, 
            user.getUsername(), 
            user.getEmail(), 
            user.getPasswordHash(),
            user.getRole(), 
            user.isEnabled(), 
            user.getId()
        );
    }
    
    public int deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
    
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }
    
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}