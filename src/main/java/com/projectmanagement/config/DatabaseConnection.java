package com.projectmanagement.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Reusable Thread-safe Database Connection Utility with HikariCP Connection Pooling.
 * Handles graceful initialization, connection recovery, and database creation.
 */
public final class DatabaseConnection {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static volatile HikariDataSource dataSource;
    private static final Object lock = new Object();

    private DatabaseConnection() {
    }

    /**
     * Initializes the connection pool with thread-safe double-checked locking.
     */
    public static void initialize() {
        if (dataSource == null) {
            synchronized (lock) {
                if (dataSource == null) {
                    try {
                        ensureDatabaseExists();

                        HikariConfig config = new HikariConfig();
                        config.setDriverClassName(AppConfig.getDbDriver());
                        config.setJdbcUrl(AppConfig.getDbUrl());
                        config.setUsername(AppConfig.getDbUsername());
                        config.setPassword(AppConfig.getDbPassword());

                        config.setMaximumPoolSize(AppConfig.getPoolMaxSize());
                        config.setMinimumIdle(AppConfig.getPoolMinIdle());
                        config.setConnectionTimeout(30000);
                        config.setIdleTimeout(600000);
                        config.setMaxLifetime(1800000);
                        config.setPoolName("ProManage-HikariPool");

                        config.addDataSourceProperty("cachePrepStmts", "true");
                        config.addDataSourceProperty("prepStmtCacheSize", "250");
                        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                        config.addDataSourceProperty("useServerPrepStmts", "true");

                        dataSource = new HikariDataSource(config);
                        logger.info("HikariCP DataSource initialized successfully on: {}", AppConfig.getDbUrl());

                        // Automatically check and run schema initialization if needed
                        initializeSchemaIfRequired();

                    } catch (Exception e) {
                        logger.error("Error initializing HikariCP DataSource: {}", e.getMessage(), e);
                        throw new RuntimeException("Database connection initialization failed: " + e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * Checks if the MySQL database exists; if not, attempts to create it via root connection.
     */
    private static void ensureDatabaseExists() {
        String host = AppConfig.get("db.host", "localhost");
        int port = AppConfig.getInt("db.port", 3306);
        String dbName = AppConfig.get("db.name", "project_management_db");
        String username = AppConfig.getDbUsername();
        String password = AppConfig.getDbPassword();

        String serverUrl = String.format("jdbc:mysql://%s:%d/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", host, port);

        try (Connection conn = DriverManager.getConnection(serverUrl, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            logger.info("Database '{}' checked/verified successfully.", dbName);
        } catch (SQLException e) {
            logger.warn("Could not auto-verify database creation at server root: {}", e.getMessage());
            // Proceed to standard pool creation attempt
        }
    }

    /**
     * Runs schema script if users table does not yet exist.
     */
    private static void initializeSchemaIfRequired() {
        try (Connection conn = getConnection()) {
            boolean tablesExist = false;
            try (Statement stmt = conn.createStatement()) {
                var rs = stmt.executeQuery("SHOW TABLES LIKE 'users'");
                if (rs.next()) {
                    tablesExist = true;
                }
            }

            if (!tablesExist) {
                logger.info("Tables not found. Initializing database schema...");
                executeSqlScript(conn, "database/schema.sql");
                executeSqlScript(conn, "database/seed.sql");
                logger.info("Database schema and seed data created successfully.");
            } else {
                logger.info("Database schema already exists.");
            }
        } catch (Exception e) {
            logger.warn("Schema auto-initialization check encountered: {}", e.getMessage());
        }
    }

    /**
     * Executes SQL script from file or classpath.
     */
    public static void executeSqlScript(Connection conn, String relativePath) {
        try {
            java.io.File file = new java.io.File(relativePath);
            InputStream is;
            if (file.exists()) {
                is = new java.io.FileInputStream(file);
            } else {
                is = DatabaseConnection.class.getClassLoader().getResourceAsStream(relativePath);
            }

            if (is == null) {
                logger.warn("SQL script not found: {}", relativePath);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                 Statement stmt = conn.createStatement()) {

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("//")) {
                        continue;
                    }
                    sb.append(line).append(" ");
                    if (line.endsWith(";")) {
                        String sql = sb.toString().trim();
                        if (sql.endsWith(";")) {
                            sql = sql.substring(0, sql.length() - 1);
                        }
                        if (!sql.isEmpty() && !sql.toLowerCase().startsWith("create database") && !sql.toLowerCase().startsWith("use ")) {
                            try {
                                stmt.execute(sql);
                            } catch (SQLException ex) {
                                logger.debug("Non-fatal SQL script error on '{}': {}", sql, ex.getMessage());
                            }
                        }
                        sb.setLength(0);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error executing SQL script {}: {}", relativePath, e.getMessage());
        }
    }

    /**
     * Obtains a connection from the connection pool.
     *
     * @return Connection instance
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initialize();
        }
        return dataSource.getConnection();
    }

    /**
     * Gracefully shuts down the connection pool upon application undeploy.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            synchronized (lock) {
                if (dataSource != null && !dataSource.isClosed()) {
                    logger.info("Closing HikariCP DataSource...");
                    dataSource.close();
                    dataSource = null;
                }
            }
        }
    }
}
