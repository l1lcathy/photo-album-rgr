package com.photoalbum.controller;

import com.photoalbum.model.AccessLevel;
import com.photoalbum.model.Album;
import com.photoalbum.security.AppUserDetails;
import com.photoalbum.service.AlbumService;
import com.photoalbum.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final UserService userService;

    public AlbumController(AlbumService albumService, UserService userService) {
        this.albumService = albumService;
        this.userService = userService;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal AppUserDetails user, Model model) {
        model.addAttribute("albums", albumService.getVisible(user.getId()));
        return "albums";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("accessLevels", AccessLevel.values());
        return "album-form";
    }

    @PostMapping("/new")
    public String create(
            @AuthenticationPrincipal AppUserDetails user,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam AccessLevel accessLevel
    ) {
        Album album = new Album();
        album.setOwnerId(user.getId());
        album.setName(name);
        album.setDescription(description);
        album.setAccessLevel(accessLevel);
        albumService.create(album);
        return "redirect:/albums";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        albumService.delete(id);
        return "redirect:/albums";
    }
}