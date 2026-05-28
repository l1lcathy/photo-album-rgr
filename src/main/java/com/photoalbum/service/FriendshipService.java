package com.photoalbum.service;

import com.photoalbum.model.Friendship;
import com.photoalbum.model.User;
import com.photoalbum.repository.jdbc.FriendshipRepositoryJdbc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepositoryJdbc friendshipRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    public void sendRequest(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new RuntimeException("Нельзя добавить самого себя");
        }
        if (friendshipRepository.isRequestExists(userId, friendId)) {
            throw new RuntimeException("Заявка уже отправлена или вы уже друзья");
        }
        friendshipRepository.sendRequest(userId, friendId);
        
        User sender = userService.findById(userId);
        User receiver = userService.findById(friendId);
        if (sender != null && receiver != null) {
            emailService.sendFriendshipRequestNotification(receiver.getEmail(), sender.getUsername());
        }
    }
    
    public List<Friendship> getPendingRequests(Long userId) {
        return friendshipRepository.getPendingRequests(userId);
    }
    
    public List<Friendship> getFriends(Long userId) {
        return friendshipRepository.getFriends(userId);
    }
    
    public List<Long> getFriendsIds(Long userId) {
        return friendshipRepository.getFriendsIds(userId);
    }
    
    public void acceptRequest(Long friendshipId) {
        friendshipRepository.acceptRequest(friendshipId);
    }
    
    public void rejectRequest(Long friendshipId) {
        friendshipRepository.rejectRequest(friendshipId);
    }
    
    public void removeFriend(Long userId, Long friendId) {
        friendshipRepository.removeFriend(userId, friendId);
    }
    
    public boolean areFriends(Long userId, Long friendId) {
        return friendshipRepository.areFriends(userId, friendId);
    }
}