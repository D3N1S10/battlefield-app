package com.battlefield.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseManager {

    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private final HikariDataSource dataSource;

    public DatabaseManager() {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:h2:mem:battlefield;DB_CLOSE_DELAY=-1");
        cfg.setUsername("sa");
        cfg.setPassword("");
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setDriverClassName("org.h2.Driver");
        this.dataSource = new HikariDataSource(cfg);
        initSchema();
        log.info("Database initialized (H2 in-memory)");
    }

    private void initSchema() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("init.sql")) {
            if (is == null) throw new RuntimeException("init.sql not found on classpath");
            String sql = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                for (String s : sql.split(";")) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty()) stmt.execute(trimmed);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to init DB schema", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("Database pool closed");
        }
    }
}
