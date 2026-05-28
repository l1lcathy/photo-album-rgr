package com.photoalbum.service;

import com.photoalbum.model.Album;
import com.photoalbum.repository.jdbc.AlbumRepositoryJdbc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepositoryJdbc albumRepository;
    
    @Autowired
    private FriendshipService friendshipService;

    public Album create(Album album) {
        return albumRepository.save(album);
    }

    public List<Album> getByOwner(Long ownerId) {
        return albumRepository.findByOwnerId(ownerId);
    }
    
    // Получить доступные альбомы для пользователя (свои + публичные + альбомы друзей)
    public List<Album> getAccessibleAlbums(Long userId) {
        List<Long> friendsIds = friendshipService.getFriendsIds(userId);
        return albumRepository.findAccessibleAlbums(userId, friendsIds);
    }
    
    // Проверить доступ к альбому
    public boolean hasAccess(Long albumId, Long userId) {
        List<Long> friendsIds = friendshipService.getFriendsIds(userId);
        return albumRepository.hasAccess(albumId, userId, friendsIds);
    }

    public Album findById(Long id) {
        return albumRepository.findById(id).orElse(null);
    }
    
    public Album update(Album album) {
        albumRepository.update(album);
        return album;
    }

    public void delete(Long id) {
        albumRepository.deleteById(id);
    }
}