package com.projectmanagement.model;

/**
 * User Roles with authorization utility methods.
 */
public enum Role {
    ADMIN("Administrator", "badge bg-danger"),
    PROJECT_MANAGER("Project Manager", "badge bg-primary"),
    TEAM_MEMBER("Team Member", "badge bg-info");

    private final String displayName;
    private final String badgeClass;

    Role(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public static Role fromString(String roleStr) {
        if (roleStr == null) return TEAM_MEMBER;
        try {
            return Role.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return TEAM_MEMBER;
        }
    }
}
