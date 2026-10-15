package com.projectmanagement.model;

/**
 * System Setting Entity for global application configuration.
 */
public class SystemSetting extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String settingKey;
    private String settingValue;

    public SystemSetting() {
        super();
    }

    public SystemSetting(String settingKey, String settingValue) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
}
