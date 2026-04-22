package com.erp.financial.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public record DatabaseConfig(String host, int port, String databaseName, String username, String password) {
    public static DatabaseConfig load() {
        Properties properties = new Properties();
        Path external = Path.of("database.properties");
        Path template = Path.of("database.properties.template");

        try {
            if (Files.exists(external)) {
                try (InputStream inputStream = Files.newInputStream(external)) {
                    properties.load(inputStream);
                }
            } else if (Files.exists(template)) {
                try (InputStream inputStream = Files.newInputStream(template)) {
                    properties.load(inputStream);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load database configuration.", exception);
        }

        return new DatabaseConfig(
                properties.getProperty("db.host", "127.0.0.1"),
                Integer.parseInt(properties.getProperty("db.port", "3306")),
                properties.getProperty("db.name", "financial_management"),
                properties.getProperty("db.username", "root"),
                properties.getProperty("db.password", "")
        );
    }

    public String serverJdbcUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/?allowMultiQueries=true&serverTimezone=UTC";
    }

    public String databaseJdbcUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?allowMultiQueries=true&serverTimezone=UTC";
    }
}

