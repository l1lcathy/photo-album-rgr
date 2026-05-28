package com.photoalbum.controller;

import com.photoalbum.service.PhotoService;
import com.photoalbum.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @Autowired
    private PhotoService photoService;
    
    @Autowired
    private TagService tagService;

    @GetMapping("/search")
    public String search(
            @RequestParam(value = "tag", required = false) String tag,
            Model model) {
        
        if (tag != null && !tag.trim().isEmpty()) {
            model.addAttribute("photos", photoService.searchPhotosByTag(tag.trim()));
            model.addAttribute("currentTag", tag);
        }
        
        model.addAttribute("popularTags", tagService.getPopularTags());
        return "search";
    }
}