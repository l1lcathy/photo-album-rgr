package com.photoalbum.service;

import com.photoalbum.model.Album;
import com.photoalbum.repository.jdbc.AlbumRepositoryJdbc;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {

    private final AlbumRepositoryJdbc albumRepository;

    public AlbumService(AlbumRepositoryJdbc albumRepository) {
        this.albumRepository = albumRepository;
    }

    public Album create(Album album) {
        return albumRepository.save(album);
    }

    public List<Album> getByOwner(Long ownerId) {
        return albumRepository.findByOwnerId(ownerId);
    }

    public List<Album> getVisible(Long userId) {
        // Пока возвращаем только публичные альбомы
        // TODO: добавить логику для "друзей" позже
        return albumRepository.findPublicAlbums();
    }

    public void delete(Long id) {
        albumRepository.deleteById(id);
    }
    
    public Album findById(Long id) {
        return albumRepository.findById(id).orElse(null);
    }
    
    public Album update(Album album) {
        albumRepository.update(album);
        return album;
    }
}