package com.projectmanagement.dao.impl;

import com.projectmanagement.config.DatabaseConnection;
import com.projectmanagement.dao.ProjectMemberDAO;
import com.projectmanagement.exception.DatabaseException;
import com.projectmanagement.model.AccountStatus;
import com.projectmanagement.model.ProjectMember;
import com.projectmanagement.model.Role;
import com.projectmanagement.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectMemberDAOImpl implements ProjectMemberDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProjectMemberDAOImpl.class);

    @Override
    public List<ProjectMember> findMembersByProjectId(Long projectId) {
        List<ProjectMember> list = new ArrayList<>();
        String sql = "SELECT pm.id, pm.project_id, pm.user_id, pm.joined_at, " +
                "u.name AS user_name, u.email AS user_email, u.role AS user_role " +
                "FROM project_members pm " +
                "JOIN users u ON pm.user_id = u.id " +
                "WHERE pm.project_id = ? " +
                "ORDER BY u.name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProjectMember member = new ProjectMember();
                    member.setId(rs.getLong("id"));
                    member.setProjectId(rs.getLong("project_id"));
                    member.setUserId(rs.getLong("user_id"));
                    member.setJoinedAt(rs.getTimestamp("joined_at"));
                    member.setUserName(rs.getString("user_name"));
                    member.setUserEmail(rs.getString("user_email"));
                    member.setUserRole(Role.fromString(rs.getString("user_role")));
                    list.add(member);
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error fetching project members for project {}: {}", projectId, e.getMessage());
            throw new DatabaseException("Failed to fetch project members", e);
        }
    }

    @Override
    public List<User> findUsersNotAssignedToProject(Long projectId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users " +
                "WHERE account_status = 'ACTIVE' " +
                "AND id NOT IN (SELECT user_id FROM project_members WHERE project_id = ?) " +
                "AND id NOT IN (SELECT manager_id FROM projects WHERE id = ?) " +
                "ORDER BY name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            ps.setLong(2, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(Role.fromString(rs.getString("role")));
                    user.setAccountStatus(AccountStatus.fromString(rs.getString("account_status")));
                    list.add(user);
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error finding unassigned users for project {}: {}", projectId, e.getMessage());
            throw new DatabaseException("Failed to find available users for project", e);
        }
    }

    @Override
    public boolean addMember(Long projectId, Long userId) {
        String sql = "INSERT INTO project_members (project_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error adding user {} to project {}: {}", userId, projectId, e.getMessage());
            throw new DatabaseException("Failed to add user to project", e);
        }
    }

    @Override
    public boolean removeMember(Long projectId, Long userId) {
        String sql = "DELETE FROM project_members WHERE project_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error removing user {} from project {}: {}", userId, projectId, e.getMessage());
            throw new DatabaseException("Failed to remove user from project", e);
        }
    }

    @Override
    public boolean isUserMemberOfProject(Long projectId, Long userId) {
        String sql = "SELECT id FROM project_members WHERE project_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking user {} membership in project {}: {}", userId, projectId, e.getMessage());
            throw new DatabaseException("Failed to verify project membership", e);
        }
    }

    @Override
    public int countMembersByProjectId(Long projectId) {
        String sql = "SELECT COUNT(*) FROM project_members WHERE project_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            logger.error("Error counting members for project {}: {}", projectId, e.getMessage());
            throw new DatabaseException("Failed to count project members", e);
        }
    }
}
