package com.projectmanagement.controller;

import com.projectmanagement.model.Project;
import com.projectmanagement.model.Task;
import com.projectmanagement.model.User;
import com.projectmanagement.model.dto.DashboardMetricsDTO;
import com.projectmanagement.model.dto.ProjectProgressDTO;
import com.projectmanagement.service.DashboardService;
import com.projectmanagement.service.ProjectService;
import com.projectmanagement.service.ServiceFactory;
import com.projectmanagement.service.TaskService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Project Manager Dashboard and Progress Reports.
 */
@WebServlet(name = "ManagerDashboardController", urlPatterns = {"/manager/dashboard", "/manager/reports"})
public class ManagerDashboardController extends HttpServlet {

    private final DashboardService dashboardService = ServiceFactory.getDashboardService();
    private final ProjectService projectService = ServiceFactory.getProjectService();
    private final TaskService taskService = ServiceFactory.getTaskService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        Long managerId = currentUser.getId();

        String servletPath = req.getServletPath();

        DashboardMetricsDTO metrics = dashboardService.getManagerDashboardMetrics(managerId);
        List<ProjectProgressDTO> progressList = dashboardService.getManagerProjectsProgress(managerId);
        List<Project> myProjects = projectService.getProjectsByManager(managerId);
        List<Task> myTasks = taskService.getTasksForManager(managerId);

        req.setAttribute("metrics", metrics);
        req.setAttribute("progressList", progressList);
        req.setAttribute("projects", myProjects);
        req.setAttribute("tasks", myTasks);

        if ("/manager/reports".equals(servletPath)) {
            req.getRequestDispatcher("/WEB-INF/views/manager/reports.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/manager/dashboard.jsp").forward(req, resp);
        }
    }
}
