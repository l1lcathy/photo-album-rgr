package com.photoalbum.repository;

import com.photoalbum.model.AccessLevel;
import com.photoalbum.model.Album;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class AlbumRepositoryOld {

    private final JdbcTemplate jdbcTemplate;

    public AlbumRepositoryOld(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Album save(Album album) {
        String sql = """
                INSERT INTO albums (owner_id, name, description, access_level)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, album.getOwnerId());
            ps.setString(2, album.getName());
            ps.setString(3, album.getDescription());
            ps.setString(4, album.getAccessLevel().name());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            album.setId(keyHolder.getKey().longValue());
        }
        return album;
    }

    public List<Album> findByOwnerId(Long ownerId) {
        return jdbcTemplate.query(
                "SELECT * FROM albums WHERE owner_id = ? ORDER BY id DESC",
                new BeanPropertyRowMapper<>(Album.class),
                ownerId
        );
    }

    public Optional<Album> findById(Long id) {
        List<Album> list = jdbcTemplate.query(
                "SELECT * FROM albums WHERE id = ?",
                new BeanPropertyRowMapper<>(Album.class),
                id
        );
        return list.stream().findFirst();
    }

    public List<Album> findPublicAndOwn(Long userId) {
        return jdbcTemplate.query(
                """
                SELECT * FROM albums
                WHERE owner_id = ? OR access_level = 'PUBLIC'
                ORDER BY id DESC
                """,
                new BeanPropertyRowMapper<>(Album.class),
                userId
        );
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM albums WHERE id = ?", id);
    }

    public void updateAccessLevel(Long id, AccessLevel accessLevel) {
        jdbcTemplate.update("UPDATE albums SET access_level = ? WHERE id = ?", accessLevel.name(), id);
    }
}