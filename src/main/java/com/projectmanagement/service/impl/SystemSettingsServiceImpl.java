package com.projectmanagement.service.impl;

import com.projectmanagement.dao.SystemSettingsDAO;
import com.projectmanagement.service.SystemSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SystemSettingsServiceImpl implements SystemSettingsService {

    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsServiceImpl.class);
    private final SystemSettingsDAO systemSettingsDAO;

    public SystemSettingsServiceImpl(SystemSettingsDAO systemSettingsDAO) {
        this.systemSettingsDAO = systemSettingsDAO;
    }

    @Override
    public Map<String, String> getSettings() {
        return systemSettingsDAO.getAllSettings();
    }

    @Override
    public String getSetting(String key, String defaultValue) {
        return systemSettingsDAO.getSetting(key).orElse(defaultValue);
    }

    @Override
    public void updateSetting(String key, String value) {
        systemSettingsDAO.updateSetting(key, value);
        logger.info("Updated setting '{}' = '{}'", key, value);
    }

    @Override
    public void updateSettings(Map<String, String> settings) {
        systemSettingsDAO.saveSettings(settings);
        logger.info("Updated {} system settings in batch.", settings.size());
    }
}
