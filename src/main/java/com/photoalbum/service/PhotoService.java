package com.photoalbum.service;

import com.photoalbum.model.Photo;
import com.photoalbum.model.Tag;
import com.photoalbum.model.Album;
import com.photoalbum.model.User;
import com.photoalbum.repository.jdbc.PhotoRepositoryJdbc;
import com.photoalbum.repository.jdbc.TagRepositoryJdbc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoService {

    private final PhotoRepositoryJdbc photoRepository;
    private final TagRepositoryJdbc tagRepository;
    
    @Autowired
    private AlbumService albumService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;

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
    
    public void updateTagsForPhoto(Long photoId, String tagsString) {
        tagRepository.deleteTagsForPhoto(photoId);
        if (tagsString != null && !tagsString.trim().isEmpty()) {
            addTagsToPhoto(photoId, tagsString);
        }
        System.out.println("Теги обновлены для фото " + photoId);
    }
    
    public List<Photo> searchPhotosByTag(String tagName) {
        Tag tag = tagRepository.findByName(tagName);
        if (tag == null) {
            return new ArrayList<>();
        }
        return photoRepository.findByTagId(tag.getId());
    }
    
    public List<Photo> searchPhotosByAuthor(String username) {
        return photoRepository.findByUsername(username);
    }
    
    public List<Photo> searchPhotosByDate(LocalDateTime from, LocalDateTime to) {
        return photoRepository.findByDateRange(from, to);
    }
    
    public List<Photo> getAllPhotos() {
        return photoRepository.findAll();
    }
    
    public void copyPhoto(Long sourcePhotoId, Long targetAlbumId, Long userId, String tagsString) {
        Photo sourcePhoto = getPhotoById(sourcePhotoId);
        if (sourcePhoto == null) {
            throw new RuntimeException("Фото не найдено");
        }
        
        Album targetAlbum = albumService.findById(targetAlbumId);
        if (targetAlbum == null || !targetAlbum.getOwnerId().equals(userId)) {
            throw new RuntimeException("Нет доступа к этому альбому");
        }
        
        String sourcePath = sourcePhoto.getImagePath();
        String fileName = sourcePath.substring(sourcePath.lastIndexOf("/"));
        String newFileName = System.currentTimeMillis() + "_copy" + fileName;
        String newImagePath = "/uploads/" + newFileName;
        
        String basePath = System.getProperty("user.dir");
        String sourceFile = basePath + File.separator + "uploads" + File.separator + 
                            sourcePath.substring(sourcePath.lastIndexOf("/") + 1);
        String destFile = basePath + File.separator + "uploads" + File.separator + newFileName;
        
        try {
            java.nio.file.Files.copy(new File(sourceFile).toPath(), 
                                      new File(destFile).toPath(),
                                      java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка копирования файла: " + e.getMessage());
        }
        
        Photo newPhoto = new Photo();
        newPhoto.setTitle(sourcePhoto.getTitle() + " (копия)");
        newPhoto.setDescription(sourcePhoto.getDescription());
        newPhoto.setImagePath(newImagePath);
        newPhoto.setAlbumId(targetAlbumId);
        newPhoto.setUserId(userId);
        newPhoto.setRating(0);
        newPhoto.setCreatedAt(LocalDateTime.now());
        
        Photo savedPhoto = photoRepository.save(newPhoto);
        
        List<Tag> tags = tagRepository.getTagsByPhotoId(sourcePhotoId);
        for (Tag tag : tags) {
            tagRepository.addTagToPhoto(savedPhoto.getId(), tag.getId());
        }
        
        if (tagsString != null && !tagsString.trim().isEmpty()) {
            addTagsToPhoto(savedPhoto.getId(), tagsString);
        }
        
        User photoOwner = userService.findById(sourcePhoto.getUserId());
        User currentUser = userService.findById(userId);
        if (photoOwner != null && currentUser != null && !photoOwner.getId().equals(userId)) {
            emailService.sendCopyNotification(photoOwner.getEmail(), 
                                              currentUser.getUsername(),
                                              sourcePhoto.getTitle(),
                                              targetAlbum.getName());
        }
        
        System.out.println("Фото скопировано: " + sourcePhoto.getTitle() + " -> альбом " + targetAlbum.getName());
    }
}