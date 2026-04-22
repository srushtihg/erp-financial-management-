package com.erp.financial.db;

import com.erp.financial.config.DatabaseConfig;
import com.erp.financial.exception.DataAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseBootstrap {
    private final ConnectionFactory connectionFactory;
    private final DatabaseConfig config;

    public DatabaseBootstrap(ConnectionFactory connectionFactory, DatabaseConfig config) {
        this.connectionFactory = connectionFactory;
        this.config = config;
    }

    public void initialize() {
        try (Connection connection = connectionFactory.openServerConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS " + config.databaseName());
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to create or access the database schema.", exception);
        }

        String script;
        try {
            script = Files.readString(Path.of("resources", "financial_management_schema.sql"));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load resources/financial_management_schema.sql", exception);
        }

        new SqlScriptRunner(connectionFactory).run(script);
    }
}

