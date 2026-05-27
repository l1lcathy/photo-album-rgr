package com.photoalbum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepository {

    private final JdbcTemplate jdbcTemplate;

    public TagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long findOrCreate(String name) {
        List<Long> ids = jdbcTemplate.query(
                "SELECT id FROM tags WHERE LOWER(name) = LOWER(?)",
                (rs, rowNum) -> rs.getLong("id"),
                name
        );

        if (!ids.isEmpty()) {
            return ids.get(0);
        }

        jdbcTemplate.update("INSERT INTO tags(name) VALUES (?)", name);

        return jdbcTemplate.queryForObject(
                "SELECT id FROM tags WHERE LOWER(name) = LOWER(?)",
                Long.class,
                name
        );
    }

    public void addTagToPhoto(Long photoId, Long tagId) {
        jdbcTemplate.update(
                "INSERT INTO photo_tags(photo_id, tag_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                photoId, tagId
        );
    }

    public List<String> findTagsByPhotoId(Long photoId) {
        return jdbcTemplate.query(
                """
                SELECT t.name
                FROM tags t
                JOIN photo_tags pt ON pt.tag_id = t.id
                WHERE pt.photo_id = ?
                ORDER BY t.name
                """,
                (rs, rowNum) -> rs.getString("name"),
                photoId
        );
    }
}