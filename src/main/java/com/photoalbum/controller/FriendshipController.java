package com.photoalbum.controller;

import com.photoalbum.model.User;
import com.photoalbum.service.FriendshipService;
import com.photoalbum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/friends")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;
    
    @Autowired
    private UserService userService;
    
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username);
        return user.getId();
    }
    
    @GetMapping
    public String friendsPage(Model model) {
        Long currentUserId = getCurrentUserId();
        model.addAttribute("friends", friendshipService.getFriends(currentUserId));
        model.addAttribute("pendingRequests", friendshipService.getPendingRequests(currentUserId));
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("allUsers", userService.getAllUsers());
        return "friends";
    }
    
    @PostMapping("/add")
    public String sendRequestById(@RequestParam Long friendId) {
        Long currentUserId = getCurrentUserId();
        try {
            friendshipService.sendRequest(currentUserId, friendId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/friends";
    }
    
    @PostMapping("/add/{friendId}")
    public String sendRequest(@PathVariable Long friendId) {
        Long currentUserId = getCurrentUserId();
        try {
            friendshipService.sendRequest(currentUserId, friendId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/friends";
    }
    
    @PostMapping("/accept/{friendshipId}")
    public String acceptRequest(@PathVariable Long friendshipId) {
        friendshipService.acceptRequest(friendshipId);
        return "redirect:/friends";
    }
    
    @PostMapping("/reject/{friendshipId}")
    public String rejectRequest(@PathVariable Long friendshipId) {
        friendshipService.rejectRequest(friendshipId);
        return "redirect:/friends";
    }
    
    @PostMapping("/remove/{friendId}")
    public String removeFriend(@PathVariable Long friendId) {
        Long currentUserId = getCurrentUserId();
        friendshipService.removeFriend(currentUserId, friendId);
        return "redirect:/friends";
    }
}