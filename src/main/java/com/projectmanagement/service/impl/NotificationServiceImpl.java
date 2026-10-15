package com.projectmanagement.service.impl;

import com.projectmanagement.dao.NotificationDAO;
import com.projectmanagement.model.Notification;
import com.projectmanagement.service.NotificationService;
import com.projectmanagement.util.BackgroundJobExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final NotificationDAO notificationDAO;

    public NotificationServiceImpl(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    @Override
    public void notifyUser(Long userId, String message, String type) {
        if (userId == null || message == null) return;
        BackgroundJobExecutor.execute(() -> {
            try {
                Notification n = new Notification(userId, message, type);
                notificationDAO.create(n);
                logger.debug("Dispatched notification to user {}: {}", userId, message);
            } catch (Exception e) {
                logger.error("Failed to asynchronously persist notification: {}", e.getMessage());
            }
        });
    }

    @Override
    public List<Notification> getUserNotifications(Long userId, int limit) {
        return notificationDAO.findByUserId(userId, limit);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationDAO.findUnreadByUserId(userId);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return notificationDAO.countUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        notificationDAO.markAsRead(notificationId, userId);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationDAO.markAllAsRead(userId);
    }
}
