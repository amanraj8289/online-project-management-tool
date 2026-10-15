package com.projectmanagement.model;

public enum TaskStatus {
    TODO("To Do", "badge bg-secondary"),
    IN_PROGRESS("In Progress", "badge bg-primary"),
    IN_REVIEW("In Review", "badge bg-warning text-dark"),
    COMPLETED("Completed", "badge bg-success");

    private final String displayName;
    private final String badgeClass;

    TaskStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public static TaskStatus fromString(String statusStr) {
        if (statusStr == null) return TODO;
        try {
            return TaskStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return TODO;
        }
    }
}
