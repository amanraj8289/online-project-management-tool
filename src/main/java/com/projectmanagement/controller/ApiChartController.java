package com.projectmanagement.controller;

import com.google.gson.Gson;
import com.projectmanagement.model.User;
import com.projectmanagement.model.dto.DashboardMetricsDTO;
import com.projectmanagement.model.dto.ProjectProgressDTO;
import com.projectmanagement.service.DashboardService;
import com.projectmanagement.service.ServiceFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * RESTful JSON API Controller providing dynamic metrics for Chart.js visualizations.
 */
@WebServlet(name = "ApiChartController", urlPatterns = {
        "/api/charts/task-distribution",
        "/api/charts/project-progress",
        "/api/charts/manager-metrics"
})
public class ApiChartController extends HttpServlet {

    private final DashboardService dashboardService = ServiceFactory.getDashboardService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Unauthorized\"}");
            return;
        }

        String path = req.getServletPath();

        if ("/api/charts/task-distribution".equals(path)) {
            DashboardMetricsDTO metrics;
            if (currentUser.isAdmin()) {
                metrics = dashboardService.getAdminDashboardMetrics();
            } else if (currentUser.isProjectManager()) {
                metrics = dashboardService.getManagerDashboardMetrics(currentUser.getId());
            } else {
                metrics = dashboardService.getMemberDashboardMetrics(currentUser.getId());
            }
            resp.getWriter().write(gson.toJson(metrics.getTaskStatusDistribution()));
            return;
        }

        if ("/api/charts/project-progress".equals(path)) {
            List<ProjectProgressDTO> progressList;
            if (currentUser.isProjectManager()) {
                progressList = dashboardService.getManagerProjectsProgress(currentUser.getId());
            } else {
                progressList = dashboardService.getProjectsProgress();
            }
            resp.getWriter().write(gson.toJson(progressList));
            return;
        }

        if ("/api/charts/manager-metrics".equals(path)) {
            DashboardMetricsDTO metrics = dashboardService.getManagerDashboardMetrics(currentUser.getId());
            resp.getWriter().write(gson.toJson(metrics));
        }
    }
}
