package com.photoalbum.controller;

import com.photoalbum.service.PhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    private final PhotoService photoService;

    public SearchController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        if (q != null && !q.isBlank()) {
            model.addAttribute("photos", photoService.search(q));
        }
        return "search";
    }
}