package com.projectmanagement.model;

/**
 * Task Comment Entity representing collaborative notes/updates on tasks.
 */
public class TaskComment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long taskId;
    private Long userId;
    private String comment;

    // View-layer transient joined fields
    private String userName;
    private String userEmail;
    private Role userRole;

    public TaskComment() {
        super();
    }

    public TaskComment(Long taskId, Long userId, String comment) {
        this.taskId = taskId;
        this.userId = userId;
        this.comment = comment;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
