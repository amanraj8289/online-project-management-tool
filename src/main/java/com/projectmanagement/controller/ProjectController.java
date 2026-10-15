package com.projectmanagement.controller;

import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.model.*;
import com.projectmanagement.service.*;
import com.projectmanagement.util.DateUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Project CRUD, Team Member assignment, and Project View details.
 */
@WebServlet(name = "ProjectController", urlPatterns = {
        "/projects", "/projects/view", "/projects/create", "/projects/edit",
        "/projects/delete", "/projects/member/add", "/projects/member/remove"
})
public class ProjectController extends HttpServlet {

    private final ProjectService projectService = ServiceFactory.getProjectService();
    private final TaskService taskService = ServiceFactory.getTaskService();
    private final UserService userService = ServiceFactory.getUserService();
    private final ActivityLogService activityLogService = ServiceFactory.getActivityLogService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        String path = req.getServletPath();

        if ("/projects/create".equals(path)) {
            req.setAttribute("statuses", ProjectStatus.values());
            req.setAttribute("managers", userService.getUsersByRole(Role.PROJECT_MANAGER));
            req.setAttribute("availableMembers", userService.getUsersByRole(Role.TEAM_MEMBER));
            req.getRequestDispatcher("/WEB-INF/views/manager/project-form.jsp").forward(req, resp);
            return;
        }

        if ("/projects/edit".equals(path)) {
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                Project project = projectService.getProjectById(id);

                if (!currentUser.isAdmin() && !project.getManagerId().equals(currentUser.getId())) {
                    resp.sendRedirect(req.getContextPath() + "/projects?error=unauthorized");
                    return;
                }

                req.setAttribute("project", project);
                req.setAttribute("statuses", ProjectStatus.values());
                req.setAttribute("managers", userService.getUsersByRole(Role.PROJECT_MANAGER));
                req.setAttribute("currentMembers", projectService.getProjectMembers(id));
                req.setAttribute("availableMembers", projectService.getAvailableUsersForProject(id));
                req.getRequestDispatcher("/WEB-INF/views/manager/project-form.jsp").forward(req, resp);
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/projects?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        if ("/projects/view".equals(path)) {
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                Project project = projectService.getProjectById(id);

                if (!projectService.isUserAuthorizedForProject(id, currentUser.getId(), currentUser.getRole())) {
                    resp.sendRedirect(req.getContextPath() + "/projects?error=unauthorized");
                    return;
                }

                List<Task> tasks = taskService.getTasksByProject(id);
                List<ProjectMember> members = projectService.getProjectMembers(id);
                List<User> availableUsers = projectService.getAvailableUsersForProject(id);

                req.setAttribute("project", project);
                req.setAttribute("tasks", tasks);
                req.setAttribute("members", members);
                req.setAttribute("availableUsers", availableUsers);

                req.getRequestDispatcher("/WEB-INF/views/manager/project-details.jsp").forward(req, resp);
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/projects?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        // List projects based on role
        List<Project> projects;
        if (currentUser.isAdmin()) {
            projects = projectService.getAllProjects();
        } else if (currentUser.isProjectManager()) {
            projects = projectService.getProjectsByManager(currentUser.getId());
        } else {
            projects = projectService.getProjectsForMember(currentUser.getId());
        }

        req.setAttribute("projects", projects);

        if (currentUser.isAdmin()) {
            req.getRequestDispatcher("/WEB-INF/views/admin/projects.jsp").forward(req, resp);
        } else if (currentUser.isProjectManager()) {
            req.getRequestDispatcher("/WEB-INF/views/manager/projects.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/member/projects.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        String path = req.getServletPath();

        if ("/projects/create".equals(path)) {
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            Date startDate = DateUtils.parseSqlDate(req.getParameter("startDate"));
            Date dueDate = DateUtils.parseSqlDate(req.getParameter("dueDate"));
            String statusStr = req.getParameter("status");

            Long managerId;
            if (currentUser.isAdmin() && req.getParameter("managerId") != null && !req.getParameter("managerId").isEmpty()) {
                managerId = Long.parseLong(req.getParameter("managerId"));
            } else {
                managerId = currentUser.getId();
            }

            List<Long> memberIds = new ArrayList<>();
            String[] memberParams = req.getParameterValues("memberIds");
            if (memberParams != null) {
                for (String m : memberParams) {
                    try { memberIds.add(Long.parseLong(m)); } catch (NumberFormatException ignored) {}
                }
            }

            try {
                Project project = new Project();
                project.setTitle(title);
                project.setDescription(description);
                project.setStartDate(startDate);
                project.setDueDate(dueDate);
                project.setStatus(ProjectStatus.fromString(statusStr));
                project.setManagerId(managerId);

                Project created = projectService.createProject(project, memberIds);
                activityLogService.logActivity(currentUser.getId(), created.getId(), null, "PROJECT_CREATED",
                        "Created project: " + created.getTitle());

                resp.sendRedirect(req.getContextPath() + "/projects/view?id=" + created.getId() + "&success=Project created successfully");
            } catch (ValidationException e) {
                req.setAttribute("errorMessage", e.getMessage());
                req.setAttribute("title", title);
                req.setAttribute("description", description);
                req.setAttribute("startDate", req.getParameter("startDate"));
                req.setAttribute("dueDate", req.getParameter("dueDate"));
                req.setAttribute("statuses", ProjectStatus.values());
                req.setAttribute("managers", userService.getUsersByRole(Role.PROJECT_MANAGER));
                req.setAttribute("availableMembers", userService.getUsersByRole(Role.TEAM_MEMBER));
                req.getRequestDispatcher("/WEB-INF/views/manager/project-form.jsp").forward(req, resp);
            }
            return;
        }

        if ("/projects/edit".equals(path)) {
            Long id = Long.parseLong(req.getParameter("id"));
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            Date startDate = DateUtils.parseSqlDate(req.getParameter("startDate"));
            Date dueDate = DateUtils.parseSqlDate(req.getParameter("dueDate"));
            String statusStr = req.getParameter("status");

            Long managerId;
            if (currentUser.isAdmin() && req.getParameter("managerId") != null) {
                managerId = Long.parseLong(req.getParameter("managerId"));
            } else {
                managerId = currentUser.getId();
            }

            List<Long> memberIds = new ArrayList<>();
            String[] memberParams = req.getParameterValues("memberIds");
            if (memberParams != null) {
                for (String m : memberParams) {
                    try { memberIds.add(Long.parseLong(m)); } catch (NumberFormatException ignored) {}
                }
            }

            try {
                Project project = new Project(id, title, description, startDate, dueDate,
                        ProjectStatus.fromString(statusStr), managerId);

                projectService.updateProject(project, currentUser.getId(), currentUser.isAdmin(), memberIds);
                activityLogService.logActivity(currentUser.getId(), id, null, "PROJECT_UPDATED",
                        "Updated project: " + project.getTitle());

                resp.sendRedirect(req.getContextPath() + "/projects/view?id=" + id + "&success=Project updated successfully");
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/projects/edit?id=" + id + "&error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        if ("/projects/delete".equals(path)) {
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                projectService.deleteProject(id, currentUser.getId(), currentUser.isAdmin());
                activityLogService.logActivity(currentUser.getId(), id, null, "PROJECT_DELETED", "Deleted project ID: " + id);
                resp.sendRedirect(req.getContextPath() + "/projects?success=Project deleted successfully");
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/projects?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        if ("/projects/member/add".equals(path)) {
            Long projectId = Long.parseLong(req.getParameter("projectId"));
            Long userId = Long.parseLong(req.getParameter("userId"));
            try {
                projectService.addMemberToProject(projectId, userId, currentUser.getId(), currentUser.isAdmin());
                activityLogService.logActivity(currentUser.getId(), projectId, null, "MEMBER_ADDED",
                        "Added user ID " + userId + " to project ID " + projectId);
                resp.sendRedirect(req.getContextPath() + "/projects/view?id=" + projectId + "&success=Member added");
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/projects/view?id=" + projectId + "&error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        if ("/projects/member/remove".equals(path)) {
            Long projectId = Long.parseLong(req.getParameter("projectId"));
            Long userId = Long.parseLong(req.getParameter("userId"));
            try {
                projectService.removeMemberFromProject(projectId, userId, currentUser.getId(), currentUser.isAdmin());
                activityLogService.logActivity(currentUser.getId(), projectId, null, "MEMBER_REMOVED",
                        "Removed user ID " + userId + " from project ID " + projectId);
                resp.sendRedirect(req.getContextPath() + "/projects/view?id=" + projectId + "&success=Member removed");
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/projects/view?id=" + projectId + "&error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
        }
    }
}
