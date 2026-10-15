package com.projectmanagement.model;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Task Entity representing assignable project items with status and priority workflow.
 */
public class Task extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long projectId;
    private String title;
    private String description;
    private Long assignedTo;
    private TaskPriority priority;
    private TaskStatus status;
    private Date startDate;
    private Date dueDate;

    // View-layer transient joined fields
    private String projectTitle;
    private String assignedToName;
    private String assignedToEmail;
    private int commentCount;

    public Task() {
        super();
        this.priority = TaskPriority.MEDIUM;
        this.status = TaskStatus.TODO;
    }

    public Task(Long id, Long projectId, String title, String description, Long assignedTo,
                TaskPriority priority, TaskStatus status, Date startDate, Date dueDate) {
        super(id);
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.status = status != null ? status : TaskStatus.TODO;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    public String getAssignedToEmail() {
        return assignedToEmail;
    }

    public void setAssignedToEmail(String assignedToEmail) {
        this.assignedToEmail = assignedToEmail;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    /**
     * Checks if task is currently overdue.
     */
    public boolean isOverdue() {
        if (status == TaskStatus.COMPLETED) {
            return false;
        }
        if (dueDate == null) return false;
        return dueDate.toLocalDate().isBefore(LocalDate.now());
    }

    public boolean isCompleted() {
        return this.status == TaskStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", title='" + title + '\'' +
                ", assignedTo=" + assignedTo +
                ", priority=" + priority +
                ", status=" + status +
                ", dueDate=" + dueDate +
                '}';
    }
}
