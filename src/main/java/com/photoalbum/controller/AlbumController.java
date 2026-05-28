package com.photoalbum.controller;

import com.photoalbum.model.Album;
import com.photoalbum.model.AccessLevel;
import com.photoalbum.model.User;
import com.photoalbum.service.AlbumService;
import com.photoalbum.service.PhotoService;
import com.photoalbum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;
    
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

    @GetMapping
    public String myAlbums(Model model) {
        Long userId = getCurrentUserId();
        model.addAttribute("albums", albumService.getByOwner(userId));
        model.addAttribute("accessibleAlbums", albumService.getAccessibleAlbums(userId));
        model.addAttribute("currentUserId", userId);
        return "my_albums";
    }

    @GetMapping("/create")
    public String showCreateForm() {
        return "create_album";
    }

    @PostMapping("/create")
    public String createAlbum(@RequestParam String name, 
                              @RequestParam(defaultValue = "") String description,
                              @RequestParam(defaultValue = "PUBLIC") String accessLevel) {
        Album album = new Album();
        album.setName(name);
        album.setDescription(description);
        album.setAccessLevel(AccessLevel.valueOf(accessLevel));
        album.setOwnerId(getCurrentUserId());
        albumService.create(album);
        return "redirect:/albums";
    }

    @GetMapping("/{id}")
    public String viewAlbum(@PathVariable Long id, Model model) {
        Long currentUserId = getCurrentUserId();
        
        if (!albumService.hasAccess(id, currentUserId)) {
            return "redirect:/albums?error=Доступ запрещён";
        }
        
        Album album = albumService.findById(id);
        if (album == null) {
            return "redirect:/albums";
        }
        model.addAttribute("album", album);
        model.addAttribute("photos", photoService.getPhotosByAlbumId(id));
        return "album";
    }

    @PostMapping("/delete/{id}")
    public String deleteAlbum(@PathVariable Long id) {
        Album album = albumService.findById(id);
        if (album != null && album.getOwnerId().equals(getCurrentUserId())) {
            albumService.delete(id);
        }
        return "redirect:/albums";
    }
}