package com.projectmanagement.dao.impl;

import com.projectmanagement.config.DatabaseConnection;
import com.projectmanagement.dao.UserDAO;
import com.projectmanagement.exception.DatabaseException;
import com.projectmanagement.model.AccountStatus;
import com.projectmanagement.model.Role;
import com.projectmanagement.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.fromString(rs.getString("role")));
        user.setAccountStatus(AccountStatus.fromString(rs.getString("account_status")));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by id {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to find user by ID", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error finding user by email {}: {}", email, e.getMessage());
            throw new DatabaseException("Failed to find user by email", e);
        }
    }

    @Override
    public List<User> findAll(int offset, int limit, String searchKeyword) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM users ");
        boolean hasFilter = searchKeyword != null && !searchKeyword.trim().isEmpty();
        if (hasFilter) {
            sql.append("WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ? ");
        }
        sql.append("ORDER BY id DESC LIMIT ? OFFSET ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (hasFilter) {
                String pattern = "%" + searchKeyword.trim().toLowerCase() + "%";
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
            }
            ps.setInt(idx++, limit);
            ps.setInt(idx, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error fetching all users: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch users", e);
        }
    }

    @Override
    public int countAll(String searchKeyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users ");
        boolean hasFilter = searchKeyword != null && !searchKeyword.trim().isEmpty();
        if (hasFilter) {
            sql.append("WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (hasFilter) {
                String pattern = "%" + searchKeyword.trim().toLowerCase() + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting users: {}", e.getMessage());
            throw new DatabaseException("Failed to count users", e);
        }
    }

    @Override
    public List<User> findByRole(Role role) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? AND account_status = 'ACTIVE' ORDER BY name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error fetching users by role {}: {}", role, e.getMessage());
            throw new DatabaseException("Failed to fetch users by role", e);
        }
    }

    @Override
    public List<User> findAllActive() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE account_status = 'ACTIVE' ORDER BY name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
            return list;
        } catch (SQLException e) {
            logger.error("Error fetching active users: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch active users", e);
        }
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (name, email, password_hash, role, account_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getAccountStatus().name());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getLong(1));
                    }
                }
            }
            return user;
        } catch (SQLException e) {
            logger.error("Error creating user: {}", e.getMessage());
            throw new DatabaseException("Failed to create user", e);
        }
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, role = ?, account_status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getAccountStatus().name());
            ps.setLong(5, user.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating user {}: {}", user.getId(), e.getMessage());
            throw new DatabaseException("Failed to update user", e);
        }
    }

    @Override
    public boolean updatePassword(Long userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating password for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to update password", e);
        }
    }

    @Override
    public boolean updateProfile(Long userId, String name, String email) {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email.trim().toLowerCase());
            ps.setLong(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating profile for user {}: {}", userId, e.getMessage());
            throw new DatabaseException("Failed to update profile", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting user {}: {}", id, e.getMessage());
            throw new DatabaseException("Failed to delete user", e);
        }
    }

    @Override
    public int countActiveAdmins() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN' AND account_status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("Error counting active admins: {}", e.getMessage());
            throw new DatabaseException("Failed to count active admins", e);
        }
    }

    @Override
    public boolean isEmailTaken(String email, Long excludeUserId) {
        String sql = excludeUserId != null ?
                "SELECT id FROM users WHERE LOWER(email) = LOWER(?) AND id <> ?" :
                "SELECT id FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            if (excludeUserId != null) {
                ps.setLong(2, excludeUserId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking email uniqueness {}: {}", email, e.getMessage());
            throw new DatabaseException("Failed to check email uniqueness", e);
        }
    }

    @Override
    public int countTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            logger.error("Error counting total users: {}", e.getMessage());
            throw new DatabaseException("Failed to count total users", e);
        }
    }
}
