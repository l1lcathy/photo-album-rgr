package com.photoalbum.repository.jdbc;

import com.photoalbum.model.Friendship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class FriendshipRepositoryJdbc {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private FriendshipRowMapper friendshipRowMapper;
    
    // Отправить заявку в друзья
    public void sendRequest(Long userId, Long friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id, status, created_at) " +
                     "VALUES (?, ?, 'PENDING', ?)";
        jdbcTemplate.update(sql, userId, friendId, Timestamp.valueOf(LocalDateTime.now()));
    }
    
    // Получить все заявки для пользователя (входящие)
    public List<Friendship> getPendingRequests(Long userId) {
        String sql = "SELECT * FROM friendships WHERE friend_id = ? AND status = 'PENDING' ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, friendshipRowMapper, userId);
    }
    
    // Получить список друзей (подтверждённые)
    public List<Friendship> getFriends(Long userId) {
        String sql = "SELECT * FROM friendships WHERE (user_id = ? OR friend_id = ?) AND status = 'ACCEPTED'";
        return jdbcTemplate.query(sql, friendshipRowMapper, userId, userId);
    }
    
    // НОВЫЙ МЕТОД - получить список ID друзей
    public List<Long> getFriendsIds(Long userId) {
        String sql = "SELECT CASE WHEN user_id = ? THEN friend_id ELSE user_id END as friend_id " +
                     "FROM friendships WHERE (user_id = ? OR friend_id = ?) AND status = 'ACCEPTED'";
        return jdbcTemplate.queryForList(sql, Long.class, userId, userId, userId);
    }
    
    // Принять заявку
    public void acceptRequest(Long friendshipId) {
        String sql = "UPDATE friendships SET status = 'ACCEPTED' WHERE id = ?";
        jdbcTemplate.update(sql, friendshipId);
    }
    
    // Отклонить заявку
    public void rejectRequest(Long friendshipId) {
        String sql = "DELETE FROM friendships WHERE id = ?";
        jdbcTemplate.update(sql, friendshipId);
    }
    
    // Удалить из друзей
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        jdbcTemplate.update(sql, userId, friendId, friendId, userId);
    }
    
    // Проверить, есть ли уже заявка
    public boolean isRequestExists(Long userId, Long friendId) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId, friendId, userId);
        return count != null && count > 0;
    }
    
    // Проверить, друзья ли уже
    public boolean areFriends(Long userId, Long friendId) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE ((user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)) AND status = 'ACCEPTED'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId, friendId, userId);
        return count != null && count > 0;
    }
}