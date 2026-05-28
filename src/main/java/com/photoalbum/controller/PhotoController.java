package com.photoalbum.controller;

import com.photoalbum.model.Photo;
import com.photoalbum.model.Tag;
import com.photoalbum.model.User;
import com.photoalbum.service.AlbumService;
import com.photoalbum.service.CommentService;
import com.photoalbum.service.PhotoService;
import com.photoalbum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AlbumService albumService;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username);
        return user.getId();
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        try {
            model.addAttribute("photos", photoService.getPhotosByUserId(getCurrentUserId()));
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
            @RequestParam("albumId") Long albumId,
            @RequestParam(value = "tags", required = false) String tags
    ) {
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
            photo.setAlbumId(albumId);
            photo.setUserId(getCurrentUserId());
            photo.setRating(0);
            
            Photo savedPhoto = photoService.uploadPhoto(photo);
            
            if (tags != null && !tags.trim().isEmpty()) {
                photoService.addTagsToPhoto(savedPhoto.getId(), tags);
                System.out.println("Добавлены теги: " + tags);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "redirect:/photos/album/" + albumId;
    }
    
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        photoService.deletePhoto(id);
        return "redirect:/photos/gallery";
    }
    
    @GetMapping("/{id}")
    public String viewPhoto(@PathVariable Long id, Model model) {
        Photo photo = photoService.getPhotoById(id);
        if (photo == null) {
            return "redirect:/photos/gallery";
        }
        Long currentUserId = getCurrentUserId();
        model.addAttribute("photo", photo);
        model.addAttribute("comments", commentService.getCommentsByPhotoId(id));
        model.addAttribute("tags", photoService.getTagsByPhotoId(id));
        model.addAttribute("userAlbums", albumService.getByOwner(currentUserId));
        model.addAttribute("currentUserId", currentUserId);
        return "photo";
    }
    
    @GetMapping("/edit/{id}")
    public String editPhotoForm(@PathVariable Long id, Model model) {
        Photo photo = photoService.getPhotoById(id);
        Long currentUserId = getCurrentUserId();
        
        if (photo == null || !photo.getUserId().equals(currentUserId)) {
            return "redirect:/photos/" + id + "?error=Нет прав для редактирования";
        }
        
        List<Tag> tags = photoService.getTagsByPhotoId(id);
        String tagsString = tags.stream().map(Tag::getName).collect(Collectors.joining(", "));
        
        model.addAttribute("photo", photo);
        model.addAttribute("tagsString", tagsString);
        return "edit_photo";
    }
    
    @PostMapping("/edit/{id}")
    public String editPhoto(@PathVariable Long id,
                            @RequestParam String title,
                            @RequestParam String description,
                            @RequestParam(value = "tags", required = false) String tags) {
        Photo photo = photoService.getPhotoById(id);
        Long currentUserId = getCurrentUserId();
        
        if (photo == null || !photo.getUserId().equals(currentUserId)) {
            return "redirect:/photos/" + id + "?error=Нет прав для редактирования";
        }
        
        photo.setTitle(title);
        photo.setDescription(description);
        photoService.updatePhoto(photo);
        
        photoService.updateTagsForPhoto(id, tags);
        
        return "redirect:/photos/" + id;
    }
    
    @PostMapping("/comment/{id}")
    public String addComment(@PathVariable Long id, 
                             @RequestParam String text) {
        commentService.addComment(id, getCurrentUserId(), text);
        return "redirect:/photos/" + id;
    }
    
    @PostMapping("/rate/{id}")
    public String ratePhoto(@PathVariable Long id, @RequestParam Integer rating) {
        photoService.ratePhoto(id, rating);
        return "redirect:/photos/" + id;
    }
    
    @PostMapping("/copy/{photoId}")
    public String copyPhoto(@PathVariable Long photoId, 
                            @RequestParam Long targetAlbumId,
                            @RequestParam(value = "tags", required = false) String tags) {
        try {
            Long currentUserId = getCurrentUserId();
            photoService.copyPhoto(photoId, targetAlbumId, currentUserId, tags);
            return "redirect:/albums/" + targetAlbumId;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/photos/" + photoId + "?error=Не удалось скопировать фото";
        }
    }
}