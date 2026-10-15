package com.projectmanagement.dao.impl;

import com.projectmanagement.config.DatabaseConnection;
import com.projectmanagement.dao.ActivityLogDAO;
import com.projectmanagement.exception.DatabaseException;
import com.projectmanagement.model.ActivityLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAOImpl implements ActivityLogDAO {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogDAOImpl.class);

    private ActivityLog mapResultSetToLog(ResultSet rs) throws SQLException {
        ActivityLog log = new ActivityLog();
        log.setId(rs.getLong("id"));
        log.setUserId(rs.getLong("user_id"));

        long pId = rs.getLong("project_id");
        if (!rs.wasNull()) log.setProjectId(pId);

        long tId = rs.getLong("task_id");
        if (!rs.wasNull()) log.setTaskId(tId);

        log.setActionType(rs.getString("action_type"));
        log.setDescription(rs.getString("description"));
        log.setCreatedAt(rs.getTimestamp("created_at"));

        try {
            log.setUserName(rs.getString("user_name"));
        } catch (SQLException ignored) {}

        try {
            log.setProjectTitle(rs.getString("project_title"));
        } catch (SQLException ignored) {}

        try {
            log.setTaskTitle(rs.getString("task_title"));
        } catch (SQLException ignored) {}

        return log;
    }

    private String getLogSelectWithJoins() {
        return "SELECT al.*, u.name AS user_name, p.title AS project_title, t.title AS task_title " +
                "FROM activity_logs al " +
                "LEFT JOIN users u ON al.user_id = u.id " +
                "LEFT JOIN projects p ON al.project_id = p.id " +
                "LEFT JOIN tasks t ON al.task_id = t.id ";
    }

    @Override
    public List<ActivityLog> findRecentLogs(int limit) {
        List<ActivityLog> list = new ArrayList<>();
        String sql = getLogSelectWithJoins() + "ORDER BY al.created_at DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLog(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding recent activity logs: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch activity logs", e);
        }
    }

    @Override
    public List<ActivityLog> findByUserId(Long userId, int limit) {
        List<ActivityLog> list = new ArrayList<>();
        String sql = getLogSelectWithJoins() + "WHERE al.user_id = ? ORDER BY al.created_at DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLog(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding activity logs for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to fetch activity logs by user", e);
        }
    }

    @Override
    public List<ActivityLog> findByProjectId(Long projectId, int limit) {
        List<ActivityLog> list = new ArrayList<>();
        String sql = getLogSelectWithJoins() + "WHERE al.project_id = ? ORDER BY al.created_at DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLog(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding activity logs for project {}: {}", projectId, e.getMessage());
            throw new DatabaseException("Failed to fetch activity logs by project", e);
        }
    }

    @Override
    public ActivityLog create(ActivityLog log) {
        String sql = "INSERT INTO activity_logs (user_id, project_id, task_id, action_type, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, log.getUserId());

            if (log.getProjectId() != null) {
                ps.setLong(2, log.getProjectId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }

            if (log.getTaskId() != null) {
                ps.setLong(3, log.getTaskId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }

            ps.setString(4, log.getActionType());
            ps.setString(5, log.getDescription());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        log.setId(rs.getLong(1));
                    }
                }
            }
            return log;
        } catch (SQLException e) {
            logger.error("Error creating activity log: {}", e.getMessage());
            throw new DatabaseException("Failed to record activity log", e);
        }
    }
}
