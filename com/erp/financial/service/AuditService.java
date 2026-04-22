package com.erp.financial.service;

import com.erp.financial.db.ConnectionFactory;
import com.erp.financial.exception.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class AuditService {
    private final ConnectionFactory connectionFactory;

    public AuditService(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void log(String userId, String action, String module, String details) {
        String sql = "INSERT INTO audit_logs(user_id, action, module, details) VALUES (?, ?, ?, ?)";
        try (Connection connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            statement.setString(2, action);
            statement.setString(3, module);
            statement.setString(4, details);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to write audit log.", exception);
        }
    }
}

