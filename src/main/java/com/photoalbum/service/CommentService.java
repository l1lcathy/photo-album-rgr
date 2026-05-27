package com.photoalbum.service;

import com.photoalbum.model.Comment;
import com.photoalbum.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final EmailService emailService;
    private final UserService userService;

    public CommentService(
            CommentRepository commentRepository,
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
}