package com.projectmanagement.dao.impl;

import com.projectmanagement.config.DatabaseConnection;
import com.projectmanagement.dao.NotificationDAO;
import com.projectmanagement.exception.DatabaseException;
import com.projectmanagement.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl implements NotificationDAO {

    private static final Logger logger = LoggerFactory.getLogger(NotificationDAOImpl.class);

    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getLong("id"));
        n.setUserId(rs.getLong("user_id"));
        n.setMessage(rs.getString("message"));
        n.setNotificationType(rs.getString("notification_type"));
        n.setRead(rs.getBoolean("is_read"));
        n.setCreatedAt(rs.getTimestamp("created_at"));
        return n;
    }

    @Override
    public List<Notification> findByUserId(Long userId, int limit) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNotification(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding notifications for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to fetch notifications", e);
        }
    }

    @Override
    public List<Notification> findUnreadByUserId(Long userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNotification(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding unread notifications for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to fetch unread notifications", e);
        }
    }

    @Override
    public int countUnreadByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            logger.error("Error counting unread notifications for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to count unread notifications", e);
        }
    }

    @Override
    public Notification create(Notification notification) {
        String sql = "INSERT INTO notifications (user_id, message, notification_type, is_read) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, notification.getUserId());
            ps.setString(2, notification.getMessage());
            ps.setString(3, notification.getNotificationType());
            ps.setBoolean(4, notification.isRead());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        notification.setId(rs.getLong(1));
                    }
                }
            }
            return notification;
        } catch (SQLException e) {
            logger.error("Error creating notification: {}", e.getMessage());
            throw new DatabaseException("Failed to create notification", e);
        }
    }

    @Override
    public boolean markAsRead(Long notificationId, Long userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, notificationId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error marking notification {} as read: {}", notificationId, e.getMessage());
            throw new DatabaseException("Failed to update notification status", e);
        }
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error marking all notifications as read for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to mark all notifications as read", e);
        }
    }
}
