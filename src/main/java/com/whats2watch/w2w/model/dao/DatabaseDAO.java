package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.exceptions.DAOException;

import java.sql.*;
import java.util.Map;

public class DatabaseDAO<T> implements GenericDAO<T> {
    private final Class<T> type;
    private final Connection connection;

    public DatabaseDAO(Class<T> type) throws DAOException {
        this.type = type;
        try {
            this.connection = ConnectionManager.getConnection();
        } catch (SQLException e) {
            throw new DAOException("Error establishing database connection", e);
        }
    }

    @Override
    public Boolean save(T entity) throws DAOException {
        String tableName = type.getSimpleName().toLowerCase();
        String sql = buildInsertQuery(tableName, entity);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setPreparedStatementValues(stmt, entity);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DAOException("Error while saving entity to DB", e);
        }
    }

    @Override
    public T findById(Map<String, Object> compositeKey) throws DAOException {
        if (compositeKey == null || compositeKey.isEmpty()) {
            throw new IllegalArgumentException("Composite key cannot be null or empty.");
        }

        String tableName = type.getSimpleName().toLowerCase();
        String sql = buildFindByIdQuery(tableName, compositeKey);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setPreparedStatementValues(stmt, compositeKey);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Error while reading from DB", e);
        }
        return null;
    }

    @Override
    public Boolean delete(Map<String, Object> compositeKey) throws DAOException {
        if (compositeKey == null || compositeKey.isEmpty()) {
            throw new IllegalArgumentException("Composite key cannot be null or empty.");
        }

        String tableName = type.getSimpleName().toLowerCase();
        String sql = buildDeleteQuery(tableName, compositeKey);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setPreparedStatementValues(stmt, compositeKey);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DAOException("Error while deleting from DB", e);
        }
    }

    private String buildInsertQuery(String tableName, T entity) throws DAOException {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        try {
            for (var field : type.getDeclaredFields()) {
                field.setAccessible(true);
                columns.append(field.getName()).append(",");
                values.append("'").append(field.get(entity)).append("',");
            }

            // Remove the last comma
            columns.deleteCharAt(columns.length() - 1);
            values.deleteCharAt(values.length() - 1);

        } catch (IllegalAccessException e) {
            throw new DAOException("Error while building insert query", e);
        }

        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);
    }

    private String buildFindByIdQuery(String tableName, Map<String, Object> compositeKey) {
        StringBuilder whereClause = new StringBuilder();
        for (String key : compositeKey.keySet()) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(key).append(" = ?");
        }
        return "SELECT * FROM " + tableName + " WHERE " + whereClause;
    }

    private String buildDeleteQuery(String tableName, Map<String, Object> compositeKey) {
        StringBuilder whereClause = new StringBuilder();
        for (String key : compositeKey.keySet()) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append(key).append(" = ?");
        }
        return "DELETE FROM " + tableName + " WHERE " + whereClause;
    }

    private void setPreparedStatementValues(PreparedStatement stmt, Object obj) throws SQLException {
        int index = 1;
        if (obj instanceof Map) {
            for (Object value : ((Map<String, Object>) obj).values()) {
                stmt.setObject(index++, value);
            }
        } else {
            try {
                for (var field : type.getDeclaredFields()) {
                    field.setAccessible(true);
                    stmt.setObject(index++, field.get(obj));
                }
            } catch (IllegalAccessException e) {
                throw new SQLException("Error setting entity fields", e);
            }
        }
    }

    private T mapResultSetToEntity(ResultSet rs) throws SQLException, DAOException {
        try {
            T entity = type.getDeclaredConstructor().newInstance();
            for (var field : type.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(entity, rs.getObject(field.getName()));
            }
            return entity;
        } catch (Exception e) {
            throw new DAOException("Error mapping ResultSet to entity", e);
        }
    }
}
