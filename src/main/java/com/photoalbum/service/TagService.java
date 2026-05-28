package com.photoalbum.service;

import com.photoalbum.model.Tag;
import com.photoalbum.repository.jdbc.TagRepositoryJdbc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepositoryJdbc tagRepository;
    
    public Tag findByName(String name) {
        return tagRepository.findByName(name);
    }
    
    public List<Tag> getPopularTags() {
        return tagRepository.getPopularTags();
    }
}