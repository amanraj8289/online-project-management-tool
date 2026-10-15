package com.projectmanagement.model;

/**
 * Activity Log Entity for system-wide auditing and user activity feeds.
 */
public class ActivityLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long projectId;
    private Long taskId;
    private String actionType;
    private String description;

    // View-layer transient joined fields
    private String userName;
    private String projectTitle;
    private String taskTitle;

    public ActivityLog() {
        super();
    }

    public ActivityLog(Long userId, Long projectId, Long taskId, String actionType, String description) {
        this.userId = userId;
        this.projectId = projectId;
        this.taskId = taskId;
        this.actionType = actionType;
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
}
