package com.photoalbum.controller;

import com.photoalbum.model.Comment;
import com.photoalbum.security.AppUserDetails;
import com.photoalbum.service.CommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add")
    public String add(
            @AuthenticationPrincipal AppUserDetails user,
            @RequestParam Long photoId,
            @RequestParam String text
    ) {
        Comment comment = new Comment();
        comment.setPhotoId(photoId);
        comment.setUserId(user.getId());
        comment.setText(text);
        commentService.add(comment, user.getEmail());
        return "redirect:/photos/gallery";
    }
}