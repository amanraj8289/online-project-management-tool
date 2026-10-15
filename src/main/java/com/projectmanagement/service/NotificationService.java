package com.projectmanagement.service;

import com.projectmanagement.model.Notification;

import java.util.List;

public interface NotificationService {

    void notifyUser(Long userId, String message, String type);

    List<Notification> getUserNotifications(Long userId, int limit);

    List<Notification> getUnreadNotifications(Long userId);

    int getUnreadCount(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);
}
