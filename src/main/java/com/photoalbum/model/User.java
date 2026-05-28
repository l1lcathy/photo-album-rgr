package com.photoalbum.model;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String username;
    private String email;
    private String password;        // для формы регистрации (не хранится в БД в таком виде)
    private String passwordHash;    // хранится в БД в поле password_hash
    private String role;
    private boolean enabled;
    private LocalDateTime createdAt;

    public User() {

    }

    public User(Long id,
                String username,
                String email,
                String passwordHash,
                String role,
                boolean enabled,
                LocalDateTime createdAt) {

        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    // Getters и Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}