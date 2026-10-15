package com.projectmanagement.service.impl;

import com.projectmanagement.dao.ProjectDAO;
import com.projectmanagement.dao.TaskCommentDAO;
import com.projectmanagement.dao.TaskDAO;
import com.projectmanagement.exception.AuthorizationException;
import com.projectmanagement.exception.ResourceNotFoundException;
import com.projectmanagement.exception.ValidationException;
import com.projectmanagement.model.*;
import com.projectmanagement.service.TaskService;
import com.projectmanagement.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskDAO taskDAO;
    private final ProjectDAO projectDAO;
    private final TaskCommentDAO taskCommentDAO;

    public TaskServiceImpl(TaskDAO taskDAO, ProjectDAO projectDAO, TaskCommentDAO taskCommentDAO) {
        this.taskDAO = taskDAO;
        this.projectDAO = projectDAO;
        this.taskCommentDAO = taskCommentDAO;
    }

    @Override
    public Task getTaskById(Long id) {
        return taskDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
    }

    @Override
    public List<Task> getTasksByProject(Long projectId) {
        return taskDAO.findByProjectId(projectId);
    }

    @Override
    public List<Task> getTasksForUser(Long userId) {
        return taskDAO.findByAssignedUserId(userId);
    }

    @Override
    public List<Task> getFilteredTasksForUser(Long userId, TaskStatus status, TaskPriority priority, String search) {
        return taskDAO.findByAssignedUserIdAndFilters(userId, status, priority, search);
    }

    @Override
    public List<Task> getTasksForManager(Long managerId) {
        return taskDAO.findByManagerProjects(managerId);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskDAO.findAll();
    }

    @Override
    public Task createTask(Task task, Long creatorId) {
        ValidationUtils.validateTask(task.getTitle(), task.getProjectId(), task.getStartDate(), task.getDueDate());

        Project project = projectDAO.findById(task.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", task.getProjectId()));

        // Verification: only the project's manager or admin can create tasks
        // Creator check can be enforced at controller or here if needed

        Task created = taskDAO.create(task);
        logger.info("Task '{}' (ID: {}) created for Project ID: {}", created.getTitle(), created.getId(), created.getProjectId());
        return created;
    }

    @Override
    public void updateTask(Task task, Long updaterId, Role userRole) {
        ValidationUtils.validateTask(task.getTitle(), task.getProjectId(), task.getStartDate(), task.getDueDate());

        Task existing = getTaskById(task.getId());
        Project project = projectDAO.findById(existing.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", existing.getProjectId()));

        if (userRole != Role.ADMIN && !project.getManagerId().equals(updaterId)) {
            throw new AuthorizationException("You are not authorized to update tasks for this project.");
        }

        boolean updated = taskDAO.update(task);
        if (!updated) {
            throw new ResourceNotFoundException("Task", task.getId());
        }
        logger.info("Task '{}' (ID: {}) updated by User ID: {}", task.getTitle(), task.getId(), updaterId);
    }

    @Override
    public void updateTaskStatus(Long taskId, TaskStatus status, Long userId, Role userRole, String commentNote) {
        Task task = getTaskById(taskId);
        Project project = projectDAO.findById(task.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", task.getProjectId()));

        boolean isAssigned = task.getAssignedTo() != null && task.getAssignedTo().equals(userId);
        boolean isManager = project.getManagerId().equals(userId);
        boolean isAdmin = userRole == Role.ADMIN;

        if (!isAssigned && !isManager && !isAdmin) {
            throw new AuthorizationException("You are not authorized to change the status of this task.");
        }

        taskDAO.updateStatus(taskId, status);
        logger.info("Task ID {} status updated to {} by User ID {}", taskId, status, userId);

        if (commentNote != null && !commentNote.trim().isEmpty()) {
            addComment(taskId, userId, "Status updated to " + status.getDisplayName() + ": " + commentNote.trim());
        }
    }

    @Override
    public void deleteTask(Long taskId, Long currentUserId, boolean isAdmin) {
        Task task = getTaskById(taskId);
        Project project = projectDAO.findById(task.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", task.getProjectId()));

        if (!isAdmin && !project.getManagerId().equals(currentUserId)) {
            throw new AuthorizationException("You are not authorized to delete tasks for this project.");
        }

        boolean deleted = taskDAO.delete(taskId);
        if (!deleted) {
            throw new ResourceNotFoundException("Task", taskId);
        }
        logger.info("Task ID {} deleted by User ID {}", taskId, currentUserId);
    }

    @Override
    public List<TaskComment> getTaskComments(Long taskId) {
        return taskCommentDAO.findByTaskId(taskId);
    }

    @Override
    public TaskComment addComment(Long taskId, Long userId, String commentText) {
        if (commentText == null || commentText.trim().isEmpty()) {
            throw new ValidationException("Comment cannot be empty.");
        }

        TaskComment comment = new TaskComment(taskId, userId, commentText.trim());
        return taskCommentDAO.create(comment);
    }
}
