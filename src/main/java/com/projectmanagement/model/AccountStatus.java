package com.projectmanagement.model;

public enum AccountStatus {
    ACTIVE("Active", "badge bg-success"),
    INACTIVE("Inactive", "badge bg-secondary");

    private final String displayName;
    private final String badgeClass;

    AccountStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public static AccountStatus fromString(String statusStr) {
        if (statusStr == null) return ACTIVE;
        try {
            return AccountStatus.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}
