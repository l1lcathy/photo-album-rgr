package com.photoalbum.service;

import com.photoalbum.model.Photo;
import com.photoalbum.model.Tag;
import com.photoalbum.repository.jdbc.PhotoRepositoryJdbc;
import com.photoalbum.repository.jdbc.TagRepositoryJdbc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoService {

    private final PhotoRepositoryJdbc photoRepository;
    private final TagRepositoryJdbc tagRepository;

    public PhotoService(PhotoRepositoryJdbc photoRepository, TagRepositoryJdbc tagRepository) {
        this.photoRepository = photoRepository;
        this.tagRepository = tagRepository;
    }

    public Photo uploadPhoto(Photo photo) {
        return photoRepository.save(photo);
    }
    
    public List<Photo> getPhotosByAlbumId(Long albumId) {
        return photoRepository.findByAlbumId(albumId);
    }
    
    public List<Photo> getPhotosByUserId(Long userId) {
        return photoRepository.findByUserId(userId);
    }
    
    public Photo getPhotoById(Long id) {
        return photoRepository.findById(id).orElse(null);
    }
    
    public Photo updatePhoto(Photo photo) {
        photoRepository.update(photo);
        return photo;
    }
    
    public void deletePhoto(Long id) {
        photoRepository.deleteById(id);
    }
    
    public List<Photo> getPhotosByTagId(Long tagId) {
        return photoRepository.findByTagId(tagId);
    }
    
    public void ratePhoto(Long id, Integer rating) {
        Photo photo = getPhotoById(id);
        if (photo != null) {
            photo.setRating(rating);
            photoRepository.update(photo);
        }
    }
    
    public void addTagsToPhoto(Long photoId, String tagsString) {
        if (tagsString == null || tagsString.trim().isEmpty()) {
            return;
        }
        String[] tagNames = tagsString.split(",");
        for (String tagName : tagNames) {
            String trimmed = tagName.trim().toLowerCase();
            if (!trimmed.isEmpty()) {
                Tag tag = tagRepository.findOrCreate(trimmed);
                tagRepository.addTagToPhoto(photoId, tag.getId());
            }
        }
    }
    
    public List<Tag> getTagsByPhotoId(Long photoId) {
        return tagRepository.getTagsByPhotoId(photoId);
    }
    
    // ========== МЕТОД ДЛЯ ПОИСКА ПО ТЕГУ ==========
    
    public List<Photo> searchPhotosByTag(String tagName) {
        Tag tag = tagRepository.findByName(tagName);
        if (tag == null) {
            return new ArrayList<>();
        }
        return photoRepository.findByTagId(tag.getId());
    }
}