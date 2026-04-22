package com.erp.financial.db;

import com.erp.financial.config.DatabaseConfig;
import com.erp.financial.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
    private final DatabaseConfig config;

    public ConnectionFactory(DatabaseConfig config) {
        this.config = config;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("MySQL JDBC driver is missing from the classpath.", exception);
        }
    }

    public Connection openServerConnection() {
        return open(config.serverJdbcUrl());
    }

    public Connection openConnection() {
        return open(config.databaseJdbcUrl());
    }

    private Connection open(String url) {
        try {
            return DriverManager.getConnection(url, config.username(), config.password());
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to connect to MySQL. Check database.properties and ensure MySQL is running.", exception);
        }
    }
}

