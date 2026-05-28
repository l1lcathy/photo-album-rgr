package com.photoalbum.controller;

import com.photoalbum.model.User;
import com.photoalbum.service.PhotoService;
import com.photoalbum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PhotoService photoService;
    
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username);
        return user.getId();
    }

    @GetMapping
    public String adminPanel(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("allPhotos", photoService.getAllPhotos());
        return "admin_panel";
    }
    
    @PostMapping("/user/{id}/block")
    public String blockUser(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId.equals(id)) {
            System.out.println("Нельзя заблокировать самого себя");
            return "redirect:/admin";
        }
        User user = userService.findById(id);
        if (user != null) {
            user.setEnabled(false);
            userService.updateUser(user);
        }
        return "redirect:/admin";
    }
    
    @PostMapping("/user/{id}/unblock")
    public String unblockUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            user.setEnabled(true);
            userService.updateUser(user);
        }
        return "redirect:/admin";
    }
    
    @PostMapping("/user/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam String role) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId.equals(id)) {
            System.out.println("Нельзя изменить роль у самого себя");
            return "redirect:/admin";
        }
        System.out.println("Смена роли: user id=" + id + ", new role=" + role);
        User user = userService.findById(id);
        if (user != null) {
            user.setRole(role);
            userService.updateUser(user);
            System.out.println("Роль изменена на: " + user.getRole());
        } else {
            System.out.println("Пользователь не найден");
        }
        return "redirect:/admin";
    }
    
    @PostMapping("/photo/{id}/delete")
    public String deletePhoto(@PathVariable Long id) {
        System.out.println("Удаление фото: id=" + id);
        photoService.deletePhoto(id);
        return "redirect:/admin";
    }
}