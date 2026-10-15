package com.projectmanagement.dao;

import com.projectmanagement.model.Notification;

import java.util.List;

/**
 * Data Access Object Interface for User Notifications.
 */
public interface NotificationDAO {

    List<Notification> findByUserId(Long userId, int limit);

    List<Notification> findUnreadByUserId(Long userId);

    int countUnreadByUserId(Long userId);

    Notification create(Notification notification);

    boolean markAsRead(Long notificationId, Long userId);

    boolean markAllAsRead(Long userId);
}
