package com.projectmanagement.service;

import java.util.Map;

public interface SystemSettingsService {

    Map<String, String> getSettings();

    String getSetting(String key, String defaultValue);

    void updateSetting(String key, String value);

    void updateSettings(Map<String, String> settings);
}
