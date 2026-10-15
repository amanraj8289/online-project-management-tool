package com.projectmanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Application Configuration Manager.
 * Reads configurations from db.properties with environment variable override.
 */
public final class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static final Properties properties = new Properties();

    static {
        reload();
    }

    public static synchronized void reload() {
        properties.clear();
        java.io.File[] candidateFiles = new java.io.File[] {
            new java.io.File("db.properties"),
            new java.io.File("src/main/resources/db.properties")
        };
        boolean loaded = false;
        for (java.io.File f : candidateFiles) {
            if (f.exists() && f.isFile()) {
                try (InputStream fis = new java.io.FileInputStream(f)) {
                    properties.load(fis);
                    logger.info("Loaded db configuration from: {}", f.getAbsolutePath());
                    loaded = true;
                    break;
                } catch (Exception e) {
                    logger.warn("Could not read {}: {}", f.getName(), e.getMessage());
                }
            }
        }
        if (!loaded) {
            try (InputStream is = AppConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (is != null) {
                    properties.load(is);
                    logger.info("Loaded db.properties successfully from classpath.");
                } else {
                    logger.warn("db.properties not found on classpath, using defaults.");
                }
            } catch (Exception e) {
                logger.error("Failed to load db.properties: {}", e.getMessage());
            }
        }
    }

    private AppConfig() {
    }

    public static String get(String key, String defaultValue) {
        // First check environment variable (converting property key: db.host -> DB_HOST)
        String envKey = key.toUpperCase().replace('.', '_');
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }

        // Then check System properties
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp.trim();
        }

        // Then fallback to properties file
        return properties.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String val = get(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer for key {}: {}, using default: {}", key, val, defaultValue);
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(val);
    }

    public static String getDbUrl() {
        String host = get("db.host", "localhost");
        int port = getInt("db.port", 3306);
        String name = get("db.name", "project_management_db");
        return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                host, port, name);
    }

    public static String getDbUsername() {
        return get("db.username", "root");
    }

    public static String getDbPassword() {
        return get("db.password", "");
    }

    public static String getDbDriver() {
        return get("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    public static int getPoolMaxSize() {
        return getInt("db.pool.maximumPoolSize", 10);
    }

    public static int getPoolMinIdle() {
        return getInt("db.pool.minimumIdle", 2);
    }
}
