package com.projectmanagement.model;

/**
 * Notification Entity for alert notifications.
 */
public class Notification extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String message;
    private String notificationType;
    private boolean read;

    public Notification() {
        super();
        this.notificationType = "INFO";
        this.read = false;
    }

    public Notification(Long userId, String message, String notificationType) {
        this.userId = userId;
        this.message = message;
        this.notificationType = notificationType != null ? notificationType : "INFO";
        this.read = false;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
