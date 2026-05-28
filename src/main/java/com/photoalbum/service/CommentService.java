package com.photoalbum.service;

import com.photoalbum.model.Comment;
import com.photoalbum.model.Photo;
import com.photoalbum.model.User;
import com.photoalbum.repository.jdbc.CommentRepositoryJdbc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepositoryJdbc commentRepository;
    
    @Autowired
    private PhotoService photoService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;

    public Comment add(Comment comment, String ownerEmail) {
        Comment saved = commentRepository.save(comment);
        emailService.sendNotification(ownerEmail, "Новый комментарий к вашей фотографии");
        return saved;
    }

    public List<Comment> byPhoto(Long photoId) {
        return commentRepository.findByPhotoId(photoId);
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
    
    public List<Comment> getCommentsByPhotoId(Long photoId) {
        return commentRepository.findByPhotoId(photoId);
    }
    
    public void addComment(Long photoId, Long userId, String text) {
        Photo photo = photoService.getPhotoById(photoId);
        if (photo == null) return;
        
        User commentOwner = userService.findById(userId);
        User photoOwner = userService.findById(photo.getUserId());
        
        Comment comment = new Comment();
        comment.setPhotoId(photoId);
        comment.setUserId(userId);
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        
        if (photoOwner != null && commentOwner != null && !photoOwner.getId().equals(userId)) {
            emailService.sendCommentNotification(
                photoOwner.getEmail(),
                photo.getTitle(),
                commentOwner.getUsername(),
                text
            );
        }
    }
}