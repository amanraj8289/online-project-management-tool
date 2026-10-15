package com.projectmanagement.model;

public enum ProjectStatus {
    PLANNING("Planning", "badge bg-secondary"),
    IN_PROGRESS("In Progress", "badge bg-primary"),
    ON_HOLD("On Hold", "badge bg-warning text-dark"),
    COMPLETED("Completed", "badge bg-success"),
    CANCELLED("Cancelled", "badge bg-danger");

    private final String displayName;
    private final String badgeClass;

    ProjectStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public static ProjectStatus fromString(String statusStr) {
        if (statusStr == null) return PLANNING;
        try {
            return ProjectStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PLANNING;
        }
    }
}
