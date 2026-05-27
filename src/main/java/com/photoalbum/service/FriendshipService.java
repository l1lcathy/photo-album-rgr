package com.photoalbum.service;

import com.photoalbum.repository.FriendshipRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;

    public FriendshipService(FriendshipRepository friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    public void request(Long requesterId, Long receiverId) {
        friendshipRepository.requestFriendship(requesterId, receiverId);
    }

    public void accept(Long friendshipId) {
        friendshipRepository.accept(friendshipId);
    }

    public void delete(Long friendshipId) {
        friendshipRepository.delete(friendshipId);
    }

    public boolean areFriends(Long a, Long b) {
        return friendshipRepository.areFriends(a, b);
    }

    public List<Long> friendsIds(Long userId) {
        return friendshipRepository.findFriendsIds(userId);
    }
}