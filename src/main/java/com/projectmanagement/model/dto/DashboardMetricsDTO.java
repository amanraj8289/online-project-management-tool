package com.projectmanagement.model.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * Data Transfer Object for aggregate dashboard KPI cards and metrics.
 */
public class DashboardMetricsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int totalUsers;
    private int totalProjects;
    private int activeProjects;
    private int completedProjects;
    private int totalTasks;
    private int completedTasks;
    private int inProgressTasks;
    private int overdueTasks;
    private int pendingTasks;

    // Breakdown maps for Chart.js rendering
    private Map<String, Integer> taskStatusDistribution;
    private Map<String, Integer> taskPriorityDistribution;
    private Map<String, Integer> userRoleDistribution;

    public DashboardMetricsDTO() {
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(int totalProjects) {
        this.totalProjects = totalProjects;
    }

    public int getActiveProjects() {
        return activeProjects;
    }

    public void setActiveProjects(int activeProjects) {
        this.activeProjects = activeProjects;
    }

    public int getCompletedProjects() {
        return completedProjects;
    }

    public void setCompletedProjects(int completedProjects) {
        this.completedProjects = completedProjects;
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

    public int getInProgressTasks() {
        return inProgressTasks;
    }

    public void setInProgressTasks(int inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }

    public int getOverdueTasks() {
        return overdueTasks;
    }

    public void setOverdueTasks(int overdueTasks) {
        this.overdueTasks = overdueTasks;
    }

    public int getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(int pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public Map<String, Integer> getTaskStatusDistribution() {
        return taskStatusDistribution;
    }

    public void setTaskStatusDistribution(Map<String, Integer> taskStatusDistribution) {
        this.taskStatusDistribution = taskStatusDistribution;
    }

    public Map<String, Integer> getTaskPriorityDistribution() {
        return taskPriorityDistribution;
    }

    public void setTaskPriorityDistribution(Map<String, Integer> taskPriorityDistribution) {
        this.taskPriorityDistribution = taskPriorityDistribution;
    }

    public Map<String, Integer> getUserRoleDistribution() {
        return userRoleDistribution;
    }

    public void setUserRoleDistribution(Map<String, Integer> userRoleDistribution) {
        this.userRoleDistribution = userRoleDistribution;
    }

    public int getOverallCompletionRate() {
        if (totalTasks == 0) return 0;
        return (int) Math.round(((double) completedTasks / totalTasks) * 100);
    }
}
