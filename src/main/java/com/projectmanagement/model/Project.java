package com.projectmanagement.model;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Project Entity with timeline validation and progress calculation.
 */
public class Project extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private Date startDate;
    private Date dueDate;
    private ProjectStatus status;
    private Long managerId;

    // View-layer / Join transient fields
    private String managerName;
    private String managerEmail;
    private int totalTasks;
    private int completedTasks;
    private int teamMemberCount;

    public Project() {
        super();
        this.status = ProjectStatus.PLANNING;
    }

    public Project(Long id, String title, String description, Date startDate, Date dueDate, ProjectStatus status, Long managerId) {
        super(id);
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.status = status != null ? status : ProjectStatus.PLANNING;
        this.managerId = managerId;
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

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getTeamMemberCount() {
        return teamMemberCount;
    }

    public void setTeamMemberCount(int teamMemberCount) {
        this.teamMemberCount = teamMemberCount;
    }

    /**
     * Dynamically calculates project progress percentage based on task completion.
     */
    public int getProgressPercentage() {
        if (totalTasks == 0) {
            return status == ProjectStatus.COMPLETED ? 100 : 0;
        }
        return (int) Math.round(((double) completedTasks / totalTasks) * 100);
    }

    /**
     * Checks if project is overdue.
     */
    public boolean isOverdue() {
        if (status == ProjectStatus.COMPLETED || status == ProjectStatus.CANCELLED) {
            return false;
        }
        if (dueDate == null) return false;
        return dueDate.toLocalDate().isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", managerId=" + managerId +
                ", startDate=" + startDate +
                ", dueDate=" + dueDate +
                '}';
    }
}
