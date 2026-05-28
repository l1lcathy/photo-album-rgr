package com.photoalbum.controller;

import com.photoalbum.model.Album;
import com.photoalbum.model.AccessLevel;
import com.photoalbum.service.AlbumService;
import com.photoalbum.service.PhotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final PhotoService photoService;

    public AlbumController(AlbumService albumService, PhotoService photoService) {
        this.albumService = albumService;
        this.photoService = photoService;
    }

    @GetMapping
    public String myAlbums(Model model) {
        model.addAttribute("albums", albumService.getByOwner(7L));
        return "my_albums";  // ← ИСПРАВЛЕНО
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
        album.setOwnerId(7L);
        albumService.create(album);
        return "redirect:/albums";
    }

    @GetMapping("/{id}")
    public String viewAlbum(@PathVariable Long id, Model model) {
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
        albumService.delete(id);
        return "redirect:/albums";
    }
}