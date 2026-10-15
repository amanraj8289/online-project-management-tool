package com.projectmanagement.service.impl;

import com.projectmanagement.dao.ProjectDAO;
import com.projectmanagement.dao.TaskDAO;
import com.projectmanagement.dao.UserDAO;
import com.projectmanagement.model.Role;
import com.projectmanagement.model.dto.DashboardMetricsDTO;
import com.projectmanagement.model.dto.ProjectProgressDTO;
import com.projectmanagement.service.DashboardService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardServiceImpl implements DashboardService {

    private final UserDAO userDAO;
    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;

    public DashboardServiceImpl(UserDAO userDAO, ProjectDAO projectDAO, TaskDAO taskDAO) {
        this.userDAO = userDAO;
        this.projectDAO = projectDAO;
        this.taskDAO = taskDAO;
    }

    @Override
    public DashboardMetricsDTO getAdminDashboardMetrics() {
        DashboardMetricsDTO dto = new DashboardMetricsDTO();
        dto.setTotalUsers(userDAO.countTotalUsers());
        dto.setTotalProjects(projectDAO.countTotalProjects());
        dto.setActiveProjects(projectDAO.countActiveProjects());
        dto.setCompletedProjects(projectDAO.countCompletedProjects());

        dto.setTotalTasks(taskDAO.countTotalTasks());
        dto.setCompletedTasks(taskDAO.countCompletedTasks());
        dto.setInProgressTasks(taskDAO.countInProgressTasks());
        dto.setOverdueTasks(taskDAO.countOverdueTasks());
        dto.setPendingTasks(taskDAO.countPendingTasks());

        dto.setTaskStatusDistribution(taskDAO.getTaskStatusDistribution());
        dto.setTaskPriorityDistribution(taskDAO.getTaskPriorityDistribution());

        Map<String, Integer> roleMap = new LinkedHashMap<>();
        for (Role r : Role.values()) {
            roleMap.put(r.getDisplayName(), userDAO.findByRole(r).size());
        }
        dto.setUserRoleDistribution(roleMap);

        return dto;
    }

    @Override
    public DashboardMetricsDTO getManagerDashboardMetrics(Long managerId) {
        DashboardMetricsDTO dto = new DashboardMetricsDTO();
        dto.setTotalProjects(projectDAO.countManagerProjects(managerId));

        var projects = projectDAO.findByManagerId(managerId);
        int active = 0;
        int completed = 0;
        for (var p : projects) {
            if ("COMPLETED".equals(p.getStatus().name())) {
                completed++;
            } else if (!"CANCELLED".equals(p.getStatus().name())) {
                active++;
            }
        }
        dto.setActiveProjects(active);
        dto.setCompletedProjects(completed);

        var tasks = taskDAO.findByManagerProjects(managerId);
        int totalTasks = tasks.size();
        int completedTasks = 0;
        int inProgressTasks = 0;
        int overdueTasks = 0;
        int pendingTasks = 0;

        for (var t : tasks) {
            if (t.isCompleted()) {
                completedTasks++;
            } else {
                if (t.isOverdue()) overdueTasks++;
                if ("IN_PROGRESS".equals(t.getStatus().name())) {
                    inProgressTasks++;
                } else {
                    pendingTasks++;
                }
            }
        }

        dto.setTotalTasks(totalTasks);
        dto.setCompletedTasks(completedTasks);
        dto.setInProgressTasks(inProgressTasks);
        dto.setOverdueTasks(overdueTasks);
        dto.setPendingTasks(pendingTasks);

        dto.setTaskStatusDistribution(taskDAO.getManagerTaskStatusDistribution(managerId));

        return dto;
    }

    @Override
    public DashboardMetricsDTO getMemberDashboardMetrics(Long userId) {
        DashboardMetricsDTO dto = new DashboardMetricsDTO();
        dto.setTotalProjects(projectDAO.findByMemberUserId(userId).size());

        int total = taskDAO.countUserTasks(userId);
        int completed = taskDAO.countUserCompletedTasks(userId);
        int pending = taskDAO.countUserPendingTasks(userId);
        int overdue = taskDAO.countUserOverdueTasks(userId);

        dto.setTotalTasks(total);
        dto.setCompletedTasks(completed);
        dto.setPendingTasks(pending);
        dto.setOverdueTasks(overdue);
        dto.setInProgressTasks(total - completed - pending);

        return dto;
    }

    @Override
    public List<ProjectProgressDTO> getProjectsProgress() {
        return projectDAO.getProjectsProgressOverview();
    }

    @Override
    public List<ProjectProgressDTO> getManagerProjectsProgress(Long managerId) {
        return projectDAO.getManagerProjectsProgressOverview(managerId);
    }
}
