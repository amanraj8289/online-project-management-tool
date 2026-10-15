package com.projectmanagement.model;

import java.sql.Timestamp;

/**
 * Project Member association entity mapping users to projects.
 */
public class ProjectMember extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long projectId;
    private Long userId;
    private Timestamp joinedAt;

    // View-layer transient fields
    private String userName;
    private String userEmail;
    private Role userRole;

    public ProjectMember() {
        super();
    }

    public ProjectMember(Long projectId, Long userId) {
        this.projectId = projectId;
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Role getUserRole() {
        return userRole;
    }

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }
}
