package com.photoalbum.repository.jdbc;

import com.photoalbum.model.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class TagRepositoryJdbc {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private TagRowMapper tagRowMapper;
    
    public Tag findOrCreate(String tagName) {
        
        String findSql = "SELECT * FROM tags WHERE LOWER(name) = LOWER(?)";
        try {
            return jdbcTemplate.queryForObject(findSql, tagRowMapper, tagName);
        } catch (Exception e) {
            String insertSql = "INSERT INTO tags (name) VALUES (?) RETURNING id";
            Long generatedId = jdbcTemplate.queryForObject(insertSql, Long.class, tagName);
            Tag newTag = new Tag();
            newTag.setId(generatedId);
            newTag.setName(tagName);
            return newTag;
        }
    }
    
    public void addTagToPhoto(Long photoId, Long tagId) {
        String sql = "INSERT INTO photo_tags (photo_id, tag_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, photoId, tagId);
    }
    
    public List<Tag> getTagsByPhotoId(Long photoId) {
        String sql = "SELECT t.* FROM tags t " +
                     "JOIN photo_tags pt ON t.id = pt.tag_id " +
                     "WHERE pt.photo_id = ?";
        return jdbcTemplate.query(sql, tagRowMapper, photoId);
    }
    
    public List<Long> getPhotoIdsByTagId(Long tagId) {
        String sql = "SELECT photo_id FROM photo_tags WHERE tag_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, tagId);
    }
    
    public List<Tag> searchTags(String query) {
        String sql = "SELECT * FROM tags WHERE LOWER(name) LIKE LOWER(?) LIMIT 10";
        return jdbcTemplate.query(sql, tagRowMapper, "%" + query + "%");
    }
    
    public List<Tag> getAllTags() {
        String sql = "SELECT * FROM tags ORDER BY name";
        return jdbcTemplate.query(sql, tagRowMapper);
    }
    
    public Tag findByName(String name) {
        String sql = "SELECT * FROM tags WHERE LOWER(name) = LOWER(?)";
        try {
            return jdbcTemplate.queryForObject(sql, tagRowMapper, name);
        } catch (Exception e) {
            return null;
        }
    }
    
    public List<Tag> getPopularTags() {
        String sql = "SELECT t.*, COUNT(pt.photo_id) as cnt FROM tags t " +
                     "JOIN photo_tags pt ON t.id = pt.tag_id " +
                     "GROUP BY t.id ORDER BY cnt DESC LIMIT 10";
        return jdbcTemplate.query(sql, tagRowMapper);
    }
    
    public void deleteTagsForPhoto(Long photoId) {
        String sql = "DELETE FROM photo_tags WHERE photo_id = ?";
        jdbcTemplate.update(sql, photoId);
        System.out.println("Удалены теги для фото " + photoId);
    }
}