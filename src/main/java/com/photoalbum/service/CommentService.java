package com.photoalbum.service;

import com.photoalbum.model.Comment;
import com.photoalbum.repository.jdbc.CommentRepositoryJdbc;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepositoryJdbc commentRepository;
    private final EmailService emailService;
    private final UserService userService;

    public CommentService(
            CommentRepositoryJdbc commentRepository,
            EmailService emailService,
            UserService userService
    ) {
        this.commentRepository = commentRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

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
    
    // Получить все комментарии к фото
    public List<Comment> getCommentsByPhotoId(Long photoId) {
        return commentRepository.findByPhotoId(photoId);
    }
    
    // Добавить комментарий
    public void addComment(Long photoId, Long userId, String text) {
        Comment comment = new Comment();
        comment.setPhotoId(photoId);
        comment.setUserId(userId);
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }
}