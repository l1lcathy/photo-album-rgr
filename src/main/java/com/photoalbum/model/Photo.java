package com.photoalbum.model;

import java.time.LocalDateTime;

public class Photo {
    private Long id;
    private String title;           // название фото
    private String description;     // описание фото
    private String imagePath;       // путь к файлу
    private Long albumId;           // ID альбома
    private Long userId;            // ID владельца
    private String Username;        // ДОБАВИТЬ - имя владельца
    private Integer rating;         // рейтинг (оценка)
    private LocalDateTime createdAt; // дата загрузки

    public Photo() {}

    // Конструктор
    public Photo(Long id, String title, String description, String imagePath, 
                 Long albumId, Long userId, Integer rating, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.albumId = albumId;
        this.userId = userId;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    // Getters и Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // ДОБАВИТЬ ГЕТТЕР И СЕТТЕР ДЛЯ USERNAME
    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}