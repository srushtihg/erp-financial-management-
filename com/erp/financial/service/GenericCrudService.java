package com.erp.financial.service;

import com.erp.financial.db.ConnectionFactory;
import com.erp.financial.domain.FieldType;
import com.erp.financial.domain.TableDefinition;
import com.erp.financial.domain.TableField;
import com.erp.financial.exception.DataAccessException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public final class GenericCrudService {
    private final ConnectionFactory connectionFactory;
    private final Map<String, TableDefinition> tables;
    private final AuditService auditService;

    public GenericCrudService(ConnectionFactory connectionFactory, Map<String, TableDefinition> tables, AuditService auditService) {
        this.connectionFactory = connectionFactory;
        this.tables = tables;
        this.auditService = auditService;
    }

    public List<Map<String, Object>> readAll(String key) {
        TableDefinition table = requireTable(key);
        String sql = "SELECT * FROM " + table.tableName() + " ORDER BY 1";
        try (Connection connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapRows(resultSet);
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to fetch data from " + table.displayName(), exception);
        }
    }

    public void create(String key, Map<String, Object> values, String userId) {
        TableDefinition table = requireTable(key);
        table.validationProfile().validate(values);
        List<TableField> insertableFields = table.fields().stream()
                .filter(TableField::editable)
                .toList();

        StringJoiner columns = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");
        insertableFields.forEach(field -> {
            columns.add(field.name());
            placeholders.add("?");
        });

        String sql = "INSERT INTO " + table.tableName() + "(" + columns + ") VALUES (" + placeholders + ")";
        try (Connection connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, insertableFields, values);
            statement.executeUpdate();
            auditService.log(userId, "CREATE", table.displayName(), "Created " + values.get(table.idColumn()));
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to create record in " + table.displayName() + ". Check unique keys and required fields.", exception);
        }
    }

    public void update(String key, Map<String, Object> values, String userId) {
        TableDefinition table = requireTable(key);
        table.validationProfile().validate(values);
        List<TableField> updateFields = table.fields().stream()
                .filter(field -> field.editable() && !field.name().equals(table.idColumn()))
                .toList();

        StringJoiner assignments = new StringJoiner(", ");
        updateFields.forEach(field -> assignments.add(field.name() + " = ?"));
        String sql = "UPDATE " + table.tableName() + " SET " + assignments + " WHERE " + table.idColumn() + " = ?";

        try (Connection connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, updateFields, values);
            statement.setObject(updateFields.size() + 1, values.get(table.idColumn()));
            statement.executeUpdate();
            auditService.log(userId, "UPDATE", table.displayName(), "Updated " + values.get(table.idColumn()));
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to update record in " + table.displayName(), exception);
        }
    }

    public void delete(String key, Object idValue, String userId) {
        TableDefinition table = requireTable(key);
        String sql = "DELETE FROM " + table.tableName() + " WHERE " + table.idColumn() + " = ?";
        try (Connection connection = connectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, idValue);
            statement.executeUpdate();
            auditService.log(userId, "DELETE", table.displayName(), "Deleted " + idValue);
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to delete record from " + table.displayName(), exception);
        }
    }

    private void bind(PreparedStatement statement, List<TableField> fields, Map<String, Object> values) throws SQLException {
        for (int index = 0; index < fields.size(); index++) {
            TableField field = fields.get(index);
            Object value = values.get(field.name());
            int parameterIndex = index + 1;
            switch (field.type()) {
                case DATE -> statement.setDate(parameterIndex, Date.valueOf((LocalDate) value));
                case DECIMAL -> statement.setBigDecimal(parameterIndex, new BigDecimal(String.valueOf(value)));
                case INTEGER -> {
                    if (value == null || String.valueOf(value).isBlank()) {
                        statement.setNull(parameterIndex, java.sql.Types.INTEGER);
                    } else {
                        statement.setInt(parameterIndex, Integer.parseInt(String.valueOf(value)));
                    }
                }
                default -> statement.setString(parameterIndex, value == null ? null : String.valueOf(value));
            }
        }
    }

    private List<Map<String, Object>> mapRows(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int column = 1; column <= metaData.getColumnCount(); column++) {
                Object value = resultSet.getObject(column);
                if (value instanceof Date date) {
                    row.put(metaData.getColumnName(column), date.toLocalDate());
                } else {
                    row.put(metaData.getColumnName(column), value);
                }
            }
            rows.add(row);
        }
        return rows;
    }

    private TableDefinition requireTable(String key) {
        TableDefinition definition = tables.get(key);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown table key: " + key);
        }
        return definition;
    }
}

