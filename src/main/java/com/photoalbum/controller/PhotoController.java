package com.photoalbum.controller;

import com.photoalbum.model.Photo;
import com.photoalbum.service.CommentService;
import com.photoalbum.service.PhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
@RequestMapping("/photos")
public class PhotoController {

    private final PhotoService photoService;
    private final CommentService commentService;

    public PhotoController(PhotoService photoService, CommentService commentService) {
        this.photoService = photoService;
        this.commentService = commentService;
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        try {
            model.addAttribute("photos", photoService.getPhotosByUserId(7L));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "gallery";
    }
    
    @GetMapping("/album/{albumId}")
    public String photosByAlbum(@PathVariable Long albumId, Model model) {
        try {
            model.addAttribute("photos", photoService.getPhotosByAlbumId(albumId));
            model.addAttribute("albumId", albumId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "gallery";
    }

    @PostMapping("/upload")
    public String upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "albumId", required = false) Long albumId,
            @RequestParam(value = "tags", required = false) String tags
    ) {
        System.out.println("=== ЗАГРУЗКА ФОТО ===");
        System.out.println("Теги получены: " + tags);
        
        try {
            String basePath = System.getProperty("user.dir");
            String uploadDir = basePath + File.separator + "uploads" + File.separator;
            
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String path = uploadDir + filename;
            
            file.transferTo(new File(path));
            
            Photo photo = new Photo();
            photo.setTitle(file.getOriginalFilename());
            photo.setDescription("");
            photo.setImagePath("/uploads/" + filename);
            photo.setAlbumId(albumId != null ? albumId : 3L);
            photo.setUserId(7L);
            photo.setRating(0);
            
            Photo savedPhoto = photoService.uploadPhoto(photo);
            System.out.println("Фото сохранено с ID: " + savedPhoto.getId());
            
            // Обработка тегов
            if (tags != null && !tags.trim().isEmpty()) {
                System.out.println("Обрабатываем теги: " + tags);
                photoService.addTagsToPhoto(savedPhoto.getId(), tags);
                System.out.println("Теги обработаны");
            } else {
                System.out.println("Теги пустые или null");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (albumId != null) {
            return "redirect:/photos/album/" + albumId;
        }
        return "redirect:/photos/gallery";
    }
    
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        photoService.deletePhoto(id);
        return "redirect:/photos/gallery";
    }
    
    // Просмотр фото с комментариями
    @GetMapping("/{id}")
    public String viewPhoto(@PathVariable Long id, Model model) {
        Photo photo = photoService.getPhotoById(id);
        if (photo == null) {
            return "redirect:/photos/gallery";
        }
        model.addAttribute("photo", photo);
        model.addAttribute("comments", commentService.getCommentsByPhotoId(id));
        model.addAttribute("tags", photoService.getTagsByPhotoId(id));
        System.out.println("Загружено тегов для фото " + id + ": " + photoService.getTagsByPhotoId(id).size());
        return "photo";
    }
    
    // Добавление комментария
    @PostMapping("/comment/{id}")
    public String addComment(@PathVariable Long id, 
                             @RequestParam String text) {
        commentService.addComment(id, 7L, text);
        return "redirect:/photos/" + id;
    }
    
    // Оценка фото (рейтинг)
    @PostMapping("/rate/{id}")
    public String ratePhoto(@PathVariable Long id, @RequestParam Integer rating) {
        photoService.ratePhoto(id, rating);
        return "redirect:/photos/" + id;
    }
}