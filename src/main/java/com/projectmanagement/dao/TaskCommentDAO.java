package com.projectmanagement.dao;

import com.projectmanagement.model.TaskComment;

import java.util.List;

/**
 * Data Access Object Interface for Task Comment collaboration.
 */
public interface TaskCommentDAO {

    List<TaskComment> findByTaskId(Long taskId);

    TaskComment create(TaskComment comment);

    boolean delete(Long id);

    int countCommentsByTaskId(Long taskId);
}
