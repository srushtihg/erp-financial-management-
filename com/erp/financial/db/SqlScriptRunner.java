package com.erp.financial.db;

import com.erp.financial.exception.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SqlScriptRunner {
    private final ConnectionFactory connectionFactory;

    public SqlScriptRunner(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void run(String script) {
        String[] statements = script.split(";");
        try (Connection connection = connectionFactory.openConnection();
             Statement statement = connection.createStatement()) {
            for (String candidate : statements) {
                String sql = sanitize(candidate);
                if (!sql.isBlank()) {
                    statement.execute(sql);
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to initialize the financial management schema.", exception);
        }
    }

    private String sanitize(String statement) {
        StringBuilder builder = new StringBuilder();
        for (String line : statement.split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("--")) {
                builder.append(line).append(System.lineSeparator());
            }
        }
        return builder.toString().trim()
                .replace("CREATE DATABASE IF NOT EXISTS financial_management", "")
                .replace("USE financial_management", "")
                .replace("INSERT INTO users VALUES", "INSERT IGNORE INTO users VALUES")
                .trim();
    }
}
