package com.projectmanagement.controller;

import com.projectmanagement.model.ActivityLog;
import com.projectmanagement.model.dto.DashboardMetricsDTO;
import com.projectmanagement.model.dto.ProjectProgressDTO;
import com.projectmanagement.service.ActivityLogService;
import com.projectmanagement.service.DashboardService;
import com.projectmanagement.service.ServiceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Administrator Dashboard analytics and metrics.
 */
@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardController extends HttpServlet {

    private final DashboardService dashboardService = ServiceFactory.getDashboardService();
    private final ActivityLogService activityLogService = ServiceFactory.getActivityLogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DashboardMetricsDTO metrics = dashboardService.getAdminDashboardMetrics();
        List<ProjectProgressDTO> progressList = dashboardService.getProjectsProgress();
        List<ActivityLog> recentLogs = activityLogService.getRecentLogs(10);

        req.setAttribute("metrics", metrics);
        req.setAttribute("progressList", progressList);
        req.setAttribute("recentLogs", recentLogs);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
