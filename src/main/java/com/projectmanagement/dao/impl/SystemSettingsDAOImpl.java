package com.projectmanagement.dao.impl;

import com.projectmanagement.config.DatabaseConnection;
import com.projectmanagement.dao.SystemSettingsDAO;
import com.projectmanagement.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class SystemSettingsDAOImpl implements SystemSettingsDAO {

    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsDAOImpl.class);

    @Override
    public Map<String, String> getAllSettings() {
        Map<String, String> settings = new LinkedHashMap<>();
        String sql = "SELECT setting_key, setting_value FROM system_settings ORDER BY setting_key ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                settings.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
            return settings;
        } catch (SQLException e) {
            logger.error("Error fetching all system settings: {}", e.getMessage());
            throw new DatabaseException("Failed to fetch system settings", e);
        }
    }

    @Override
    public Optional<String> getSetting(String key) {
        String sql = "SELECT setting_value FROM system_settings WHERE setting_key = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("setting_value"));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error fetching system setting {}: {}", key, e.getMessage());
            throw new DatabaseException("Failed to fetch system setting", e);
        }
    }

    @Override
    public boolean updateSetting(String key, String value) {
        String sql = "INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating system setting {}: {}", key, e.getMessage());
            throw new DatabaseException("Failed to update system setting", e);
        }
    }

    @Override
    public boolean saveSettings(Map<String, String> settings) {
        if (settings == null || settings.isEmpty()) return true;

        String sql = "INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean autoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Map.Entry<String, String> entry : settings.entrySet()) {
                    ps.setString(1, entry.getKey());
                    ps.setString(2, entry.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
                conn.setAutoCommit(autoCommit);
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                conn.setAutoCommit(autoCommit);
                logger.error("Transaction rolled back while saving settings batch: {}", ex.getMessage());
                throw new DatabaseException("Failed to save settings batch", ex);
            }
        } catch (SQLException e) {
            logger.error("Database connection error while saving settings batch: {}", e.getMessage());
            throw new DatabaseException("Failed to save system settings batch", e);
        }
    }
}
