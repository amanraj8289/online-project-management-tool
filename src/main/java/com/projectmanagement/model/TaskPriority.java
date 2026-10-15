package com.projectmanagement.model;

public enum TaskPriority {
    LOW("Low", "badge bg-secondary"),
    MEDIUM("Medium", "badge bg-info text-dark"),
    HIGH("High", "badge bg-warning text-dark"),
    CRITICAL("Critical", "badge bg-danger");

    private final String displayName;
    private final String badgeClass;

    TaskPriority(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public static TaskPriority fromString(String priorityStr) {
        if (priorityStr == null) return MEDIUM;
        try {
            return TaskPriority.valueOf(priorityStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return MEDIUM;
        }
    }
}
