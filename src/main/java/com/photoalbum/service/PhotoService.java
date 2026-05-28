package com.photoalbum.service;

import com.photoalbum.model.Photo;
import com.photoalbum.repository.jdbc.PhotoRepositoryJdbc;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoService {

    private final PhotoRepositoryJdbc photoRepository;

    public PhotoService(PhotoRepositoryJdbc photoRepository) {
        this.photoRepository = photoRepository;
    }

    // Загрузить новое фото
    public Photo uploadPhoto(Photo photo) {
        return photoRepository.save(photo);
    }
    
    // Найти все фото в альбоме
    public List<Photo> getPhotosByAlbumId(Long albumId) {
        return photoRepository.findByAlbumId(albumId);
    }
    
    // Найти все фото пользователя
    public List<Photo> getPhotosByUserId(Long userId) {
        return photoRepository.findByUserId(userId);
    }
    
    // Найти фото по ID
    public Photo getPhotoById(Long id) {
        return photoRepository.findById(id).orElse(null);
    }
    
    public void ratePhoto(Long id, Integer rating) {
        Photo photo = getPhotoById(id);
        if (photo != null) {
            photo.setRating(rating);
            photoRepository.update(photo);
        }
    }
    
    // Обновить фото (название, описание, рейтинг)
    public Photo updatePhoto(Photo photo) {
        photoRepository.update(photo);
        return photo;
    }
    
    // Удалить фото
    public void deletePhoto(Long id) {
        photoRepository.deleteById(id);
    }
    
    // Найти фото по тегу
    public List<Photo> getPhotosByTagId(Long tagId) {
        return photoRepository.findByTagId(tagId);
    }
}