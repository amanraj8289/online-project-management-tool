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
import java.util.List;

/**
 * Controller for Task CRUD operations, task collaboration comments, and status transitions.
 */
@WebServlet(name = "TaskController", urlPatterns = {
        "/tasks", "/tasks/view", "/tasks/create", "/tasks/edit",
        "/tasks/delete", "/tasks/status", "/tasks/comment"
})
public class TaskController extends HttpServlet {

    private final TaskService taskService = ServiceFactory.getTaskService();
    private final ProjectService projectService = ServiceFactory.getProjectService();
    private final UserService userService = ServiceFactory.getUserService();
    private final ActivityLogService activityLogService = ServiceFactory.getActivityLogService();
    private final NotificationService notificationService = ServiceFactory.getNotificationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        String path = req.getServletPath();

        if ("/tasks/create".equals(path)) {
            String projectIdStr = req.getParameter("projectId");
            if (projectIdStr != null) {
                try {
                    Long projectId = Long.parseLong(projectIdStr);
                    req.setAttribute("selectedProjectId", projectId);
                    req.setAttribute("projectMembers", projectService.getProjectMembers(projectId));
                } catch (NumberFormatException ignored) {}
            }

            List<Project> projects = currentUser.isAdmin() ?
                    projectService.getAllProjects() :
                    projectService.getProjectsByManager(currentUser.getId());

            req.setAttribute("projects", projects);
            req.setAttribute("priorities", TaskPriority.values());
            req.setAttribute("statuses", TaskStatus.values());
            req.setAttribute("allUsers", userService.getAllActiveUsers());
            req.getRequestDispatcher("/WEB-INF/views/manager/task-form.jsp").forward(req, resp);
            return;
        }

        if ("/tasks/edit".equals(path)) {
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                Task task = taskService.getTaskById(id);

                req.setAttribute("task", task);
                req.setAttribute("projects", currentUser.isAdmin() ? projectService.getAllProjects() : projectService.getProjectsByManager(currentUser.getId()));
                req.setAttribute("projectMembers", projectService.getProjectMembers(task.getProjectId()));
                req.setAttribute("priorities", TaskPriority.values());
                req.setAttribute("statuses", TaskStatus.values());
                req.setAttribute("allUsers", userService.getAllActiveUsers());
                req.getRequestDispatcher("/WEB-INF/views/manager/task-form.jsp").forward(req, resp);
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/tasks?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        if ("/tasks/view".equals(path)) {
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                Task task = taskService.getTaskById(id);
                List<TaskComment> comments = taskService.getTaskComments(id);
                Project project = projectService.getProjectById(task.getProjectId());

                req.setAttribute("task", task);
                req.setAttribute("comments", comments);
                req.setAttribute("project", project);
                req.setAttribute("statuses", TaskStatus.values());

                if (currentUser.isTeamMember()) {
                    req.getRequestDispatcher("/WEB-INF/views/member/task-details.jsp").forward(req, resp);
                } else {
                    req.getRequestDispatcher("/WEB-INF/views/manager/task-details.jsp").forward(req, resp);
                }
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/tasks?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        // List tasks
        List<Task> tasks;
        if (currentUser.isAdmin()) {
            tasks = taskService.getAllTasks();
        } else if (currentUser.isProjectManager()) {
            tasks = taskService.getTasksForManager(currentUser.getId());
        } else {
            tasks = taskService.getTasksForUser(currentUser.getId());
        }

        req.setAttribute("tasks", tasks);
        if (currentUser.isTeamMember()) {
            resp.sendRedirect(req.getContextPath() + "/member/my-tasks");
        } else {
            req.getRequestDispatcher("/WEB-INF/views/manager/tasks.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");
        String path = req.getServletPath();

        if ("/tasks/create".equals(path)) {
            Long projectId = Long.parseLong(req.getParameter("projectId"));
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            String assignedToStr = req.getParameter("assignedTo");
            Long assignedTo = (assignedToStr != null && !assignedToStr.trim().isEmpty()) ? Long.parseLong(assignedToStr) : null;
            TaskPriority priority = TaskPriority.fromString(req.getParameter("priority"));
            TaskStatus status = TaskStatus.fromString(req.getParameter("status"));
            Date startDate = DateUtils.parseSqlDate(req.getParameter("startDate"));
            Date dueDate = DateUtils.parseSqlDate(req.getParameter("dueDate"));

            try {
                Task task = new Task(null, projectId, title, description, assignedTo, priority, status, startDate, dueDate);
                Task created = taskService.createTask(task, currentUser.getId());

                activityLogService.logActivity(currentUser.getId(), projectId, created.getId(), "TASK_CREATED",
                        "Created task: " + created.getTitle());

                if (assignedTo != null) {
                    notificationService.notifyUser(assignedTo, "You have been assigned to task: \"" + created.getTitle() + "\"", "TASK_ASSIGNED");
                }

                resp.sendRedirect(req.getContextPath() + "/projects/view?id=" + projectId + "&success=Task created successfully");
            } catch (ValidationException e) {
                req.setAttribute("errorMessage", e.getMessage());
                doGet(req, resp);
            }
            return;
        }

        if ("/tasks/edit".equals(path)) {
            Long id = Long.parseLong(req.getParameter("id"));
            Long projectId = Long.parseLong(req.getParameter("projectId"));
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            String assignedToStr = req.getParameter("assignedTo");
            Long assignedTo = (assignedToStr != null && !assignedToStr.trim().isEmpty()) ? Long.parseLong(assignedToStr) : null;
            TaskPriority priority = TaskPriority.fromString(req.getParameter("priority"));
            TaskStatus status = TaskStatus.fromString(req.getParameter("status"));
            Date startDate = DateUtils.parseSqlDate(req.getParameter("startDate"));
            Date dueDate = DateUtils.parseSqlDate(req.getParameter("dueDate"));

            try {
                Task task = new Task(id, projectId, title, description, assignedTo, priority, status, startDate, dueDate);
                taskService.updateTask(task, currentUser.getId(), currentUser.getRole());

                activityLogService.logActivity(currentUser.getId(), projectId, id, "TASK_UPDATED",
                        "Updated task: " + task.getTitle());

                resp.sendRedirect(req.getContextPath() + "/tasks/view?id=" + id + "&success=Task updated successfully");
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/tasks/edit?id=" + id + "&error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        if ("/tasks/status".equals(path)) {
            Long taskId = Long.parseLong(req.getParameter("taskId"));
            TaskStatus status = TaskStatus.fromString(req.getParameter("status"));
            String note = req.getParameter("note");

            try {
                taskService.updateTaskStatus(taskId, status, currentUser.getId(), currentUser.getRole(), note);
                activityLogService.logActivity(currentUser.getId(), null, taskId, "TASK_STATUS_UPDATED",
                        "Updated task status to " + status.getDisplayName());

                String referer = req.getHeader("Referer");
                if (referer != null && !referer.isEmpty()) {
                    resp.sendRedirect(referer);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/tasks/view?id=" + taskId + "&success=Status updated");
                }
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/tasks/view?id=" + taskId + "&error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        if ("/tasks/comment".equals(path)) {
            Long taskId = Long.parseLong(req.getParameter("taskId"));
            String comment = req.getParameter("comment");

            try {
                taskService.addComment(taskId, currentUser.getId(), comment);
                activityLogService.logActivity(currentUser.getId(), null, taskId, "TASK_COMMENT_ADDED",
                        "Added progress note on task ID: " + taskId);

                resp.sendRedirect(req.getContextPath() + "/tasks/view?id=" + taskId + "&success=Comment posted");
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/tasks/view?id=" + taskId + "&error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        if ("/tasks/delete".equals(path)) {
            try {
                Long id = Long.parseLong(req.getParameter("id"));
                Task task = taskService.getTaskById(id);
                Long projectId = task.getProjectId();

                taskService.deleteTask(id, currentUser.getId(), currentUser.isAdmin());
                activityLogService.logActivity(currentUser.getId(), projectId, id, "TASK_DELETED", "Deleted task ID: " + id);

                resp.sendRedirect(req.getContextPath() + "/projects/view?id=" + projectId + "&success=Task deleted successfully");
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/tasks?error=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
        }
    }
}
