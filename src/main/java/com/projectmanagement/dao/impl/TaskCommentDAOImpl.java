package com.projectmanagement.dao.impl;

import com.projectmanagement.config.DatabaseConnection;
import com.projectmanagement.dao.TaskCommentDAO;
import com.projectmanagement.exception.DatabaseException;
import com.projectmanagement.model.Role;
import com.projectmanagement.model.TaskComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskCommentDAOImpl implements TaskCommentDAO {

    private static final Logger logger = LoggerFactory.getLogger(TaskCommentDAOImpl.class);

    @Override
    public List<TaskComment> findByTaskId(Long taskId) {
        List<TaskComment> list = new ArrayList<>();
        String sql = "SELECT tc.*, u.name AS user_name, u.email AS user_email, u.role AS user_role " +
                "FROM task_comments tc " +
                "JOIN users u ON tc.user_id = u.id " +
                "WHERE tc.task_id = ? " +
                "ORDER BY tc.created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskComment c = new TaskComment();
                    c.setId(rs.getLong("id"));
                    c.setTaskId(rs.getLong("task_id"));
                    c.setUserId(rs.getLong("user_id"));
                    c.setComment(rs.getString("comment"));
                    c.setCreatedAt(rs.getTimestamp("created_at"));
                    c.setUserName(rs.getString("user_name"));
                    c.setUserEmail(rs.getString("user_email"));
                    c.setUserRole(Role.fromString(rs.getString("user_role")));
                    list.add(c);
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding comments for task {}: {}", taskId, e.getMessage());
            throw new DatabaseException("Failed to fetch task comments", e);
        }
    }

    @Override
    public TaskComment create(TaskComment comment) {
        String sql = "INSERT INTO task_comments (task_id, user_id, comment) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, comment.getTaskId());
            ps.setLong(2, comment.getUserId());
            ps.setString(3, comment.getComment());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        comment.setId(rs.getLong(1));
                    }
                }
            }
            return comment;
        } catch (SQLException e) {
            logger.error("Error creating task comment: {}", e.getMessage());
            throw new DatabaseException("Failed to add comment", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM task_comments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting comment {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to delete comment", e);
        }
    }

    @Override
    public int countCommentsByTaskId(Long taskId) {
        String sql = "SELECT COUNT(*) FROM task_comments WHERE task_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            logger.error("Error counting comments for task {}: {}", taskId, e.getMessage());
            throw new DatabaseException("Failed to count comments", e);
        }
    }
}
