package com.photoalbum.controller;

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

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {

        try {
            model.addAttribute(
                    "photos",
                    photoService.getAllPhotos()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "gallery";
    }

    @PostMapping("/upload")
    public String upload(
            @RequestParam("file") MultipartFile file
    ) {

        try {

            String uploadDir = "uploads/";

            File dir = new File(uploadDir);

            if (!dir.exists()) {
                dir.mkdir();
            }

            String path =
                    uploadDir + file.getOriginalFilename();

            file.transferTo(new File(path));

            photoService.uploadPhoto(
                    file.getOriginalFilename(),
                    path,
                    1
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/photos/gallery";
    }
}