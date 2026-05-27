package com.photoalbum.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FriendshipRepository {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void requestFriendship(Long requesterId, Long receiverId) {
        jdbcTemplate.update(
                "INSERT INTO friendships (requester_id, receiver_id, status) VALUES (?, ?, 'PENDING')",
                requesterId, receiverId
        );
    }

    public void accept(Long friendshipId) {
        jdbcTemplate.update(
                "UPDATE friendships SET status = 'ACCEPTED' WHERE id = ?",
                friendshipId
        );
    }

    public void delete(Long friendshipId) {
        jdbcTemplate.update("DELETE FROM friendships WHERE id = ?", friendshipId);
    }

    public boolean areFriends(Long user1, Long user2) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM friendships
                WHERE status = 'ACCEPTED'
                  AND (
                        (requester_id = ? AND receiver_id = ?)
                     OR (requester_id = ? AND receiver_id = ?)
                  )
                """,
                Integer.class,
                user1, user2, user2, user1
        );
        return count != null && count > 0;
    }

    public List<Long> findFriendsIds(Long userId) {
        return jdbcTemplate.query(
                """
                SELECT CASE
                         WHEN requester_id = ? THEN receiver_id
                         ELSE requester_id
                       END AS friend_id
                FROM friendships
                WHERE status = 'ACCEPTED'
                  AND (requester_id = ? OR receiver_id = ?)
                """,
                (rs, rowNum) -> rs.getLong("friend_id"),
                userId, userId, userId
        );
    }
}