package com.projectmanagement.controller;

import com.projectmanagement.model.ActivityLog;
import com.projectmanagement.model.Project;
import com.projectmanagement.model.Task;
import com.projectmanagement.model.User;
import com.projectmanagement.model.dto.DashboardMetricsDTO;
import com.projectmanagement.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Team Member Dashboard and Personal Activity Feed.
 */
@WebServlet(name = "MemberDashboardController", urlPatterns = {"/member/dashboard", "/member/activity"})
public class MemberDashboardController extends HttpServlet {

    private final DashboardService dashboardService = ServiceFactory.getDashboardService();
    private final TaskService taskService = ServiceFactory.getTaskService();
    private final ProjectService projectService = ServiceFactory.getProjectService();
    private final ActivityLogService activityLogService = ServiceFactory.getActivityLogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        Long userId = currentUser.getId();

        String servletPath = req.getServletPath();

        DashboardMetricsDTO metrics = dashboardService.getMemberDashboardMetrics(userId);
        List<Task> myTasks = taskService.getTasksForUser(userId);
        List<Project> myProjects = projectService.getProjectsForMember(userId);
        List<ActivityLog> userLogs = activityLogService.getUserLogs(userId, 15);

        req.setAttribute("metrics", metrics);
        req.setAttribute("tasks", myTasks);
        req.setAttribute("projects", myProjects);
        req.setAttribute("logs", userLogs);

        if ("/member/activity".equals(servletPath)) {
            req.getRequestDispatcher("/WEB-INF/views/member/activity.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/member/dashboard.jsp").forward(req, resp);
        }
    }
}
