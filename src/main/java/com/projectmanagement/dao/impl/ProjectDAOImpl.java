package com.projectmanagement.dao.impl;

import com.projectmanagement.config.DatabaseConnection;
import com.projectmanagement.dao.ProjectDAO;
import com.projectmanagement.exception.DatabaseException;
import com.projectmanagement.model.Project;
import com.projectmanagement.model.ProjectStatus;
import com.projectmanagement.model.dto.ProjectProgressDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectDAOImpl implements ProjectDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDAOImpl.class);

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getLong("id"));
        project.setTitle(rs.getString("title"));
        project.setDescription(rs.getString("description"));
        project.setStartDate(rs.getDate("start_date"));
        project.setDueDate(rs.getDate("due_date"));
        project.setStatus(ProjectStatus.fromString(rs.getString("status")));
        project.setManagerId(rs.getLong("manager_id"));
        project.setCreatedAt(rs.getTimestamp("created_at"));
        project.setUpdatedAt(rs.getTimestamp("updated_at"));

        try {
            project.setManagerName(rs.getString("manager_name"));
        } catch (SQLException ignored) {}

        try {
            project.setManagerEmail(rs.getString("manager_email"));
        } catch (SQLException ignored) {}

        try {
            project.setTotalTasks(rs.getInt("total_tasks"));
        } catch (SQLException ignored) {}

        try {
            project.setCompletedTasks(rs.getInt("completed_tasks"));
        } catch (SQLException ignored) {}

        try {
            project.setTeamMemberCount(rs.getInt("member_count"));
        } catch (SQLException ignored) {}

        return project;
    }

    private String getProjectSelectWithJoins() {
        return "SELECT p.*, u.name AS manager_name, u.email AS manager_email, " +
                "(SELECT COUNT(*) FROM tasks t WHERE t.project_id = p.id) AS total_tasks, " +
                "(SELECT COUNT(*) FROM tasks t WHERE t.project_id = p.id AND t.status = 'COMPLETED') AS completed_tasks, " +
                "(SELECT COUNT(*) FROM project_members pm WHERE pm.project_id = p.id) AS member_count " +
                "FROM projects p " +
                "LEFT JOIN users u ON p.manager_id = u.id ";
    }

    @Override
    public Optional<Project> findById(Long id) {
        String sql = getProjectSelectWithJoins() + "WHERE p.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProject(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding project by id {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to find project by ID", e);
        }
    }

    @Override
    public List<Project> findAll() {
        List<Project> list = new ArrayList<>();
        String sql = getProjectSelectWithJoins() + "ORDER BY p.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProject(rs));
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding all projects: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch all projects", e);
        }
    }

    @Override
    public List<Project> findByManagerId(Long managerId) {
        List<Project> list = new ArrayList<>();
        String sql = getProjectSelectWithJoins() + "WHERE p.manager_id = ? ORDER BY p.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProject(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding projects for manager {}: {}", managerId, e.getMessage());
            throw new DatabaseException("Failed to fetch projects by manager", e);
        }
    }

    @Override
    public List<Project> findByMemberUserId(Long userId) {
        List<Project> list = new ArrayList<>();
        String sql = getProjectSelectWithJoins() +
                "INNER JOIN project_members pm ON p.id = pm.project_id " +
                "WHERE pm.user_id = ? ORDER BY p.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProject(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding projects for member {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to fetch projects by member", e);
        }
    }

    @Override
    public Project create(Project project) {
        String sql = "INSERT INTO projects (title, description, start_date, due_date, status, manager_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, project.getTitle());
            ps.setString(2, project.getDescription());
            ps.setDate(3, project.getStartDate());
            ps.setDate(4, project.getDueDate());
            ps.setString(5, project.getStatus().name());
            ps.setLong(6, project.getManagerId());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        project.setId(rs.getLong(1));
                    }
                }
            }
            return project;
        } catch (SQLException e) {
            logger.error("Error creating project: {}", e.getMessage());
            throw new DatabaseException("Failed to create project", e);
        }
    }

    @Override
    public boolean update(Project project) {
        String sql = "UPDATE projects SET title = ?, description = ?, start_date = ?, due_date = ?, status = ?, manager_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, project.getTitle());
            ps.setString(2, project.getDescription());
            ps.setDate(3, project.getStartDate());
            ps.setDate(4, project.getDueDate());
            ps.setString(5, project.getStatus().name());
            ps.setLong(6, project.getManagerId());
            ps.setLong(7, project.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating project {}: {}", project.getId(), e.getMessage());
            throw new DatabaseException("Failed to update project", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting project {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to delete project", e);
        }
    }

    @Override
    public int countTotalProjects() {
        String sql = "SELECT COUNT(*) FROM projects";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            logger.error("Error counting total projects: {}", e.getMessage());
            throw new DatabaseException("Failed to count total projects", e);
        }
    }

    @Override
    public int countActiveProjects() {
        String sql = "SELECT COUNT(*) FROM projects WHERE status IN ('IN_PROGRESS', 'PLANNING', 'ON_HOLD')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            logger.error("Error counting active projects: {}", e.getMessage());
            throw new DatabaseException("Failed to count active projects", e);
        }
    }

    @Override
    public int countCompletedProjects() {
        String sql = "SELECT COUNT(*) FROM projects WHERE status = 'COMPLETED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            logger.error("Error counting completed projects: {}", e.getMessage());
            throw new DatabaseException("Failed to count completed projects", e);
        }
    }

    @Override
    public int countManagerProjects(Long managerId) {
        String sql = "SELECT COUNT(*) FROM projects WHERE manager_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            logger.error("Error counting projects for manager {}: {}", managerId, e.getMessage());
            throw new DatabaseException("Failed to count manager projects", e);
        }
    }

    @Override
    public List<ProjectProgressDTO> getProjectsProgressOverview() {
        String sql = "SELECT p.id, p.title, p.status, p.due_date, " +
                "COUNT(t.id) AS total_tasks, " +
                "SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_tasks, " +
                "SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS in_progress_tasks, " +
                "SUM(CASE WHEN t.status IN ('TODO', 'IN_REVIEW') THEN 1 ELSE 0 END) AS todo_tasks " +
                "FROM projects p " +
                "LEFT JOIN tasks t ON p.id = t.project_id " +
                "GROUP BY p.id, p.title, p.status, p.due_date " +
                "ORDER BY p.id ASC";
        return fetchProjectProgressList(sql, null);
    }

    @Override
    public List<ProjectProgressDTO> getManagerProjectsProgressOverview(Long managerId) {
        String sql = "SELECT p.id, p.title, p.status, p.due_date, " +
                "COUNT(t.id) AS total_tasks, " +
                "SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_tasks, " +
                "SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS in_progress_tasks, " +
                "SUM(CASE WHEN t.status IN ('TODO', 'IN_REVIEW') THEN 1 ELSE 0 END) AS todo_tasks " +
                "FROM projects p " +
                "LEFT JOIN tasks t ON p.id = t.project_id " +
                "WHERE p.manager_id = ? " +
                "GROUP BY p.id, p.title, p.status, p.due_date " +
                "ORDER BY p.id ASC";
        return fetchProjectProgressList(sql, managerId);
    }

    private List<ProjectProgressDTO> fetchProjectProgressList(String sql, Long managerId) {
        List<ProjectProgressDTO> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (managerId != null) {
                ps.setLong(1, managerId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String title = rs.getString("title");
                    String status = rs.getString("status");
                    Date dueDate = rs.getDate("due_date");
                    int total = rs.getInt("total_tasks");
                    int completed = rs.getInt("completed_tasks");
                    int inProgress = rs.getInt("in_progress_tasks");
                    int todo = rs.getInt("todo_tasks");

                    boolean overdue = false;
                    if (dueDate != null && !"COMPLETED".equals(status) && !"CANCELLED".equals(status)) {
                        overdue = dueDate.toLocalDate().isBefore(java.time.LocalDate.now());
                    }

                    ProjectProgressDTO dto = new ProjectProgressDTO(id, title, total, completed, inProgress, todo, status, overdue);
                    list.add(dto);
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error fetching project progress overview: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch project progress overview", e);
        }
    }
}
