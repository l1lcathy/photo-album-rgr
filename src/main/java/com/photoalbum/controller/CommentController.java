package com.photoalbum.controller;

import com.photoalbum.model.Comment;
import com.photoalbum.model.Photo;
import com.photoalbum.model.User;
import com.photoalbum.service.CommentService;
import com.photoalbum.service.PhotoService;
import com.photoalbum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private PhotoService photoService;
    
    @Autowired
    private UserService userService;
    
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username);
        return user.getId();
    }
    
    @PostMapping("/add/{photoId}")
    public String addComment(@PathVariable Long photoId, @RequestParam String text) {
        Photo photo = photoService.getPhotoById(photoId);
        if (photo != null) {
            commentService.addComment(photoId, getCurrentUserId(), text);
        }
        return "redirect:/photos/" + photoId;
    }
    
    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return "redirect:/photos/";
    }
}