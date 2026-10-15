package com.projectmanagement.util;

import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class H2CompatibilityTest {

    @Test
    public void testH2Compatibility() throws Exception {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:mem:test_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;NON_KEYWORDS=USER";
        try (Connection conn = DriverManager.getConnection(url, "sa", "")) {
            assertNotNull(conn);

            // Run schema.sql
            runScript(conn, "database/schema.sql");

            // Run seed.sql
            runScript(conn, "database/seed.sql");

            // Verify users
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                assertTrue(rs.next());
                int count = rs.getInt(1);
                System.out.println("H2 Seeded User Count: " + count);
                assertTrue(count > 0, "Users should be seeded");
            }

            // Verify admin user
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT name, role FROM users WHERE email = 'admin@promanage.com'")) {
                assertTrue(rs.next());
                assertEquals("System Administrator", rs.getString("name"));
                assertEquals("ADMIN", rs.getString("role"));
            }
        }
    }

    private void runScript(Connection conn, String path) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
             Statement stmt = conn.createStatement()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("//")) {
                    continue;
                }
                sb.append(line).append("\n");
                if (line.endsWith(";")) {
                    String sql = sb.toString().trim();
                    if (sql.endsWith(";")) {
                        sql = sql.substring(0, sql.length() - 1);
                    }
                    if (!sql.isEmpty() && !sql.toLowerCase().startsWith("create database") && !sql.toLowerCase().startsWith("use ")) {
                        try {
                            stmt.execute(sql);
                        } catch (Exception e) {
                            System.err.println("Notice on SQL: " + sql + " -> " + e.getMessage());
                        }
                    }
                    sb.setLength(0);
                }
            }
        }
    }
}
