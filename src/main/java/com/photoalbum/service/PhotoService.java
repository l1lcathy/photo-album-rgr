package com.photoalbum.service;

import com.photoalbum.repository.PhotoRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    public void uploadPhoto(
            String filename,
            String path,
            int userId
    ) throws SQLException {

        photoRepository.save(filename, path, userId);
    }

    public List<String> getAllPhotos() throws SQLException {
        return photoRepository.findAllPaths();
    }
}