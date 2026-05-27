package com.photoalbum.service;

import com.photoalbum.model.Album;
import com.photoalbum.repository.AlbumRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public Album create(Album album) {
        return albumRepository.save(album);
    }

    public List<Album> getByOwner(Long ownerId) {
        return albumRepository.findByOwnerId(ownerId);
    }

    public List<Album> getVisible(Long userId) {
        return albumRepository.findPublicAndOwn(userId);
    }

    public void delete(Long id) {
        albumRepository.deleteById(id);
    }
}