package com.projectmanagement.dao;

import com.projectmanagement.model.Task;
import com.projectmanagement.model.TaskPriority;
import com.projectmanagement.model.TaskStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object Interface for Task persistence and workflow queries.
 */
public interface TaskDAO {

    Optional<Task> findById(Long id);

    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssignedUserId(Long userId);

    List<Task> findByAssignedUserIdAndFilters(Long userId, TaskStatus status, TaskPriority priority, String search);

    List<Task> findByManagerProjects(Long managerId);

    List<Task> findAll();

    Task create(Task task);

    boolean update(Task task);

    boolean updateStatus(Long taskId, TaskStatus status);

    boolean delete(Long id);

    int countTotalTasks();

    int countCompletedTasks();

    int countInProgressTasks();

    int countOverdueTasks();

    int countPendingTasks();

    int countUserTasks(Long userId);

    int countUserCompletedTasks(Long userId);

    int countUserPendingTasks(Long userId);

    int countUserOverdueTasks(Long userId);

    Map<String, Integer> getTaskStatusDistribution();

    Map<String, Integer> getTaskPriorityDistribution();

    Map<String, Integer> getManagerTaskStatusDistribution(Long managerId);
}
