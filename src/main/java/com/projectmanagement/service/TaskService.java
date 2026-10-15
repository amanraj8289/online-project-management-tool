package com.projectmanagement.service;

import com.projectmanagement.model.Role;
import com.projectmanagement.model.Task;
import com.projectmanagement.model.TaskComment;
import com.projectmanagement.model.TaskPriority;
import com.projectmanagement.model.TaskStatus;

import java.util.List;

/**
 * Service Interface for Task workflow, assignments, comments, and status transitions.
 */
public interface TaskService {

    Task getTaskById(Long id);

    List<Task> getTasksByProject(Long projectId);

    List<Task> getTasksForUser(Long userId);

    List<Task> getFilteredTasksForUser(Long userId, TaskStatus status, TaskPriority priority, String search);

    List<Task> getTasksForManager(Long managerId);

    List<Task> getAllTasks();

    Task createTask(Task task, Long creatorId);

    void updateTask(Task task, Long updaterId, Role userRole);

    void updateTaskStatus(Long taskId, TaskStatus status, Long userId, Role userRole, String commentNote);

    void deleteTask(Long taskId, Long currentUserId, boolean isAdmin);

    List<TaskComment> getTaskComments(Long taskId);

    TaskComment addComment(Long taskId, Long userId, String commentText);
}
