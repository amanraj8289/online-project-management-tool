package com.projectmanagement.dao;

import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object Interface for System Settings key-value pairs.
 */
public interface SystemSettingsDAO {

    Map<String, String> getAllSettings();

    Optional<String> getSetting(String key);

    boolean updateSetting(String key, String value);

    boolean saveSettings(Map<String, String> settings);
}
