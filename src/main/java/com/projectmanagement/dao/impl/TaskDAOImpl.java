package com.projectmanagement.dao.impl;

import com.projectmanagement.config.DatabaseConnection;
import com.projectmanagement.dao.TaskDAO;
import com.projectmanagement.exception.DatabaseException;
import com.projectmanagement.model.Task;
import com.projectmanagement.model.TaskPriority;
import com.projectmanagement.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class TaskDAOImpl implements TaskDAO {

    private static final Logger logger = LoggerFactory.getLogger(TaskDAOImpl.class);

    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setProjectId(rs.getLong("project_id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));

        long assignedTo = rs.getLong("assigned_to");
        if (!rs.wasNull()) {
            task.setAssignedTo(assignedTo);
        }

        task.setPriority(TaskPriority.fromString(rs.getString("priority")));
        task.setStatus(TaskStatus.fromString(rs.getString("status")));
        task.setStartDate(rs.getDate("start_date"));
        task.setDueDate(rs.getDate("due_date"));
        task.setCreatedAt(rs.getTimestamp("created_at"));
        task.setUpdatedAt(rs.getTimestamp("updated_at"));

        try {
            task.setProjectTitle(rs.getString("project_title"));
        } catch (SQLException ignored) {}

        try {
            task.setAssignedToName(rs.getString("assigned_to_name"));
        } catch (SQLException ignored) {}

        try {
            task.setAssignedToEmail(rs.getString("assigned_to_email"));
        } catch (SQLException ignored) {}

        try {
            task.setCommentCount(rs.getInt("comment_count"));
        } catch (SQLException ignored) {}

        return task;
    }

    private String getTaskSelectWithJoins() {
        return "SELECT t.*, p.title AS project_title, u.name AS assigned_to_name, u.email AS assigned_to_email, " +
                "(SELECT COUNT(*) FROM task_comments tc WHERE tc.task_id = t.id) AS comment_count " +
                "FROM tasks t " +
                "LEFT JOIN projects p ON t.project_id = p.id " +
                "LEFT JOIN users u ON t.assigned_to = u.id ";
    }

    @Override
    public Optional<Task> findById(Long id) {
        String sql = getTaskSelectWithJoins() + "WHERE t.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTask(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding task by id {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to find task by ID", e);
        }
    }

    @Override
    public List<Task> findByProjectId(Long projectId) {
        List<Task> list = new ArrayList<>();
        String sql = getTaskSelectWithJoins() + "WHERE t.project_id = ? ORDER BY t.due_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTask(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding tasks for project {}: {}", projectId, e.getMessage());
            throw new DatabaseException("Failed to fetch tasks by project", e);
        }
    }

    @Override
    public List<Task> findByAssignedUserId(Long userId) {
        List<Task> list = new ArrayList<>();
        String sql = getTaskSelectWithJoins() + "WHERE t.assigned_to = ? ORDER BY t.due_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTask(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding tasks for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to fetch tasks by user", e);
        }
    }

    @Override
    public List<Task> findByAssignedUserIdAndFilters(Long userId, TaskStatus status, TaskPriority priority, String search) {
        List<Task> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(getTaskSelectWithJoins());
        sql.append("WHERE t.assigned_to = ? ");

        if (status != null) {
            sql.append("AND t.status = ? ");
        }
        if (priority != null) {
            sql.append("AND t.priority = ? ");
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(t.title) LIKE ? OR LOWER(t.description) LIKE ?) ");
        }
        sql.append("ORDER BY t.due_date ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setLong(idx++, userId);
            if (status != null) {
                ps.setString(idx++, status.name());
            }
            if (priority != null) {
                ps.setString(idx++, priority.name());
            }
            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTask(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding filtered tasks for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to fetch filtered tasks", e);
        }
    }

    @Override
    public List<Task> findByManagerProjects(Long managerId) {
        List<Task> list = new ArrayList<>();
        String sql = getTaskSelectWithJoins() + "WHERE p.manager_id = ? ORDER BY t.due_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTask(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding tasks for manager {}: {}", managerId, e.getMessage());
            throw new DatabaseException("Failed to fetch tasks for manager", e);
        }
    }

    @Override
    public List<Task> findAll() {
        List<Task> list = new ArrayList<>();
        String sql = getTaskSelectWithJoins() + "ORDER BY t.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTask(rs));
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding all tasks: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch all tasks", e);
        }
    }

    @Override
    public Task create(Task task) {
        String sql = "INSERT INTO tasks (project_id, title, description, assigned_to, priority, status, start_date, due_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, task.getProjectId());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());

            if (task.getAssignedTo() != null) {
                ps.setLong(4, task.getAssignedTo());
            } else {
                ps.setNull(4, Types.BIGINT);
            }

            ps.setString(5, task.getPriority().name());
            ps.setString(6, task.getStatus().name());
            ps.setDate(7, task.getStartDate());
            ps.setDate(8, task.getDueDate());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        task.setId(rs.getLong(1));
                    }
                }
            }
            return task;
        } catch (SQLException e) {
            logger.error("Error creating task: {}", e.getMessage());
            throw new DatabaseException("Failed to create task", e);
        }
    }

    @Override
    public boolean update(Task task) {
        String sql = "UPDATE tasks SET project_id = ?, title = ?, description = ?, assigned_to = ?, " +
                "priority = ?, status = ?, start_date = ?, due_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, task.getProjectId());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());

            if (task.getAssignedTo() != null) {
                ps.setLong(4, task.getAssignedTo());
            } else {
                ps.setNull(4, Types.BIGINT);
            }

            ps.setString(5, task.getPriority().name());
            ps.setString(6, task.getStatus().name());
            ps.setDate(7, task.getStartDate());
            ps.setDate(8, task.getDueDate());
            ps.setLong(9, task.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating task {}: {}", task.getId(), e.getMessage());
            throw new DatabaseException("Failed to update task", e);
        }
    }

    @Override
    public boolean updateStatus(Long taskId, TaskStatus status) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, taskId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating status for task {}: {}", taskId, e.getMessage());
            throw new DatabaseException("Failed to update task status", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting task {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to delete task", e);
        }
    }

    @Override
    public int countTotalTasks() {
        String sql = "SELECT COUNT(*) FROM tasks";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            logger.error("Error counting total tasks: {}", e.getMessage());
            throw new DatabaseException("Failed to count total tasks", e);
        }
    }

    @Override
    public int countCompletedTasks() {
        String sql = "SELECT COUNT(*) FROM tasks WHERE status = 'COMPLETED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            logger.error("Error counting completed tasks: {}", e.getMessage());
            throw new DatabaseException("Failed to count completed tasks", e);
        }
    }

    @Override
    public int countInProgressTasks() {
        String sql = "SELECT COUNT(*) FROM tasks WHERE status = 'IN_PROGRESS'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            logger.error("Error counting in-progress tasks: {}", e.getMessage());
            throw new DatabaseException("Failed to count in-progress tasks", e);
        }
    }

    @Override
    public int countOverdueTasks() {
        String sql = "SELECT COUNT(*) FROM tasks WHERE status <> 'COMPLETED' AND due_date < CURRENT_DATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            logger.error("Error counting overdue tasks: {}", e.getMessage());
            throw new DatabaseException("Failed to count overdue tasks", e);
        }
    }

    @Override
    public int countPendingTasks() {
        String sql = "SELECT COUNT(*) FROM tasks WHERE status IN ('TODO', 'IN_REVIEW')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            logger.error("Error counting pending tasks: {}", e.getMessage());
            throw new DatabaseException("Failed to count pending tasks", e);
        }
    }

    @Override
    public int countUserTasks(Long userId) {
        String sql = "SELECT COUNT(*) FROM tasks WHERE assigned_to = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            logger.error("Error counting user tasks: {}", e.getMessage());
            throw new DatabaseException("Failed to count user tasks", e);
        }
    }

    @Override
    public int countUserCompletedTasks(Long userId) {
        String sql = "SELECT COUNT(*) FROM tasks WHERE assigned_to = ? AND status = 'COMPLETED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            logger.error("Error counting user completed tasks: {}", e.getMessage());
            throw new DatabaseException("Failed to count user completed tasks", e);
        }
    }

    @Override
    public int countUserPendingTasks(Long userId) {
        String sql = "SELECT COUNT(*) FROM tasks WHERE assigned_to = ? AND status <> 'COMPLETED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            logger.error("Error counting user pending tasks: {}", e.getMessage());
            throw new DatabaseException("Failed to count user pending tasks", e);
        }
    }

    @Override
    public int countUserOverdueTasks(Long userId) {
        String sql = "SELECT COUNT(*) FROM tasks WHERE assigned_to = ? AND status <> 'COMPLETED' AND due_date < CURRENT_DATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            logger.error("Error counting user overdue tasks: {}", e.getMessage());
            throw new DatabaseException("Failed to count user overdue tasks", e);
        }
    }

    @Override
    public Map<String, Integer> getTaskStatusDistribution() {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (TaskStatus s : TaskStatus.values()) {
            map.put(s.name(), 0);
        }

        String sql = "SELECT status, COUNT(*) AS cnt FROM tasks GROUP BY status";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("status"), rs.getInt("cnt"));
            }
            return map;
        } catch (SQLException e) {
            logger.error("Error fetching task status distribution: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch task status distribution", e);
        }
    }

    @Override
    public Map<String, Integer> getTaskPriorityDistribution() {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (TaskPriority p : TaskPriority.values()) {
            map.put(p.name(), 0);
        }

        String sql = "SELECT priority, COUNT(*) AS cnt FROM tasks GROUP BY priority";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("priority"), rs.getInt("cnt"));
            }
            return map;
        } catch (SQLException e) {
            logger.error("Error fetching task priority distribution: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch task priority distribution", e);
        }
    }

    @Override
    public Map<String, Integer> getManagerTaskStatusDistribution(Long managerId) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (TaskStatus s : TaskStatus.values()) {
            map.put(s.name(), 0);
        }

        String sql = "SELECT t.status, COUNT(*) AS cnt FROM tasks t " +
                "JOIN projects p ON t.project_id = p.id " +
                "WHERE p.manager_id = ? GROUP BY t.status";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("status"), rs.getInt("cnt"));
                }
            }
            return map;
        } catch (SQLException e) {
            logger.error("Error fetching manager task status distribution: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch manager task status distribution", e);
        }
    }
}
