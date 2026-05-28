package com.photoalbum.controller;

import com.photoalbum.model.Photo;
import com.photoalbum.service.PhotoService;
import com.photoalbum.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private PhotoService photoService;
    
    @Autowired
    private TagService tagService;

    @GetMapping("/search")
    public String search(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to,
            Model model) {
        
        System.out.println("=== ПОИСК ===");
        System.out.println("type: " + type);
        System.out.println("query: " + query);
        System.out.println("from: " + from);
        System.out.println("to: " + to);
        
        model.addAttribute("popularTags", tagService.getPopularTags());
        model.addAttribute("searchType", type);
        
        if (type == null) {
            return "search";
        }
        
        switch (type) {
            case "tag":
                if (query != null && !query.trim().isEmpty()) {
                    List<Photo> photos = photoService.searchPhotosByTag(query.trim());
                    System.out.println("Найдено фото по тегу: " + photos.size());
                    model.addAttribute("photos", photos);
                    model.addAttribute("query", query);
                }
                break;
                
            case "author":
                if (query != null && !query.trim().isEmpty()) {
                    List<Photo> photos = photoService.searchPhotosByAuthor(query.trim());
                    System.out.println("Найдено фото по автору: " + photos.size());
                    model.addAttribute("photos", photos);
                    model.addAttribute("query", query);
                }
                break;
                
            case "date":
                LocalDateTime fromDate = null;
                LocalDateTime toDate = null;
                if (from != null && !from.isEmpty()) {
                    fromDate = LocalDate.parse(from).atStartOfDay();
                    System.out.println("fromDate: " + fromDate);
                }
                if (to != null && !to.isEmpty()) {
                    toDate = LocalDate.parse(to).atTime(23, 59, 59);
                    System.out.println("toDate: " + toDate);
                }
                if (fromDate != null || toDate != null) {
                    List<Photo> photos = photoService.searchPhotosByDate(fromDate, toDate);
                    System.out.println("Найдено фото по дате: " + photos.size());
                    model.addAttribute("photos", photos);
                    model.addAttribute("fromDate", from);
                    model.addAttribute("toDate", to);
                }
                break;
        }
        
        return "search";
    }
}