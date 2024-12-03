package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.annotations.ForeignKey;
import com.whats2watch.w2w.annotations.PrimaryKey;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseDAO<T> implements DAO<T> {
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
            setPreparedStatementValues(stmt, entity, false); // false: exclude primary key for inserts
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DAOException("Error while saving entity to DB", e);
        }
    }

    @Override
    public T findById(T entityId) throws DAOException {
        String tableName = type.getSimpleName().toLowerCase();
        String sql = buildFindByIdQuery(tableName);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setPreparedStatementValues(stmt, entityId, true); // true: only primary key values
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
    public Boolean deleteById(T entityId) throws DAOException {
        String tableName = type.getSimpleName().toLowerCase();
        String sql = buildDeleteQuery(tableName);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setPreparedStatementValues(stmt, entityId, true); // true: only primary key values
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DAOException("Error while deleting from DB", e);
        }
    }

    @Override
    public Boolean updateById(T entity) throws DAOException, IllegalAccessException {
        String tableName = type.getSimpleName().toLowerCase();
        String sql = buildUpdateQuery(tableName, entity);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setPreparedStatementValues(stmt, entity, false); // false: exclude primary key for SET clause
            setPreparedStatementValues(stmt, entity, true);  // true: only primary key for WHERE clause
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DAOException("Error while updating entity in DB", e);
        }
    }

    // --- Query Builders ---
    private String buildInsertQuery(String tableName, T entity) throws DAOException {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();

        try {
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(ForeignKey.class)) {
                    addForeignKeyColumns(columns, values, field, entity);
                } else if (!field.isAnnotationPresent(PrimaryKey.class)) {
                    columns.append(field.getName()).append(",");
                    values.append("?").append(",");
                }
            }
            columns.deleteCharAt(columns.length() - 1); // Remove trailing comma
            values.deleteCharAt(values.length() - 1);
        } catch (IllegalAccessException e) {
            throw new DAOException("Error while building insert query", e);
        }

        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);
    }

    private String buildUpdateQuery(String tableName, T entity) throws DAOException {
        StringBuilder setClause = new StringBuilder();
        StringBuilder whereClause = new StringBuilder();

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                whereClause.append(field.getName()).append(" = ? AND ");
            } else if (field.isAnnotationPresent(ForeignKey.class)) {
                addForeignKeyColumnsToUpdateClause(setClause, field, entity);
            } else {
                setClause.append(field.getName()).append(" = ?, ");
            }
        }
        whereClause.delete(whereClause.length() - 5, whereClause.length()); // Remove trailing " AND "
        setClause.delete(setClause.length() - 2, setClause.length()); // Remove trailing ", "

        return String.format("UPDATE %s SET %s WHERE %s", tableName, setClause, whereClause);
    }

    private String buildFindByIdQuery(String tableName) {
        StringBuilder whereClause = new StringBuilder();

        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                whereClause.append(field.getName()).append(" = ? AND ");
            }
        }
        whereClause.delete(whereClause.length() - 5, whereClause.length()); // Remove trailing " AND "
        return String.format("SELECT * FROM %s WHERE %s", tableName, whereClause);
    }

    private String buildDeleteQuery(String tableName) {
        return buildFindByIdQuery(tableName).replace("SELECT *", "DELETE");
    }

    // --- Helper Methods ---
    private void addForeignKeyColumns(StringBuilder columns, StringBuilder values, Field field, T entity) throws IllegalAccessException {
        Object fkEntity = field.get(entity);
        for (Field fkField : fkEntity.getClass().getDeclaredFields()) {
            if (fkField.isAnnotationPresent(PrimaryKey.class)) {
                fkField.setAccessible(true);
                columns.append(field.getName()).append("_fk,");
                values.append("?,");
            }
        }
    }

    private void addForeignKeyColumnsToUpdateClause(StringBuilder clause, Field field, T entity) throws DAOException {
        try {
            // Get the foreign key entity
            Object fkEntity = field.get(entity);
            if (fkEntity == null) {
                throw new DAOException("Foreign key field cannot be null: " + field.getName());
            }

            // Iterate over the fields of the foreign key entity
            for (Field fkField : fkEntity.getClass().getDeclaredFields()) {
                fkField.setAccessible(true);

                // Only process fields annotated as primary keys in the foreign entity
                if (fkField.isAnnotationPresent(PrimaryKey.class)) {
                    String fkColumnName = field.getName() + "_fk"; // Assuming naming convention: <fieldName>_fk
                    // Add for SET clause: column = ?
                    clause.append(fkColumnName).append(" = ?, ");
                }
            }
        } catch (IllegalAccessException e) {
            throw new DAOException("Error accessing foreign key fields for " + field.getName(), e);
        }
    }

    private void setPreparedStatementValues(PreparedStatement stmt, T entity, boolean isPrimaryKey) throws SQLException {
        int index = 1;
        try {
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                if (isPrimaryKey == field.isAnnotationPresent(PrimaryKey.class)) {
                    stmt.setObject(index++, field.get(entity));
                }
            }
        } catch (IllegalAccessException e) {
            throw new SQLException("Error setting PreparedStatement values", e);
        }
    }

    private T mapResultSetToEntity(ResultSet rs) throws SQLException, DAOException {
        try {
            T entity = createEntityInstance(type);

            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(ForeignKey.class)) {
                    // Identify all fields referencing the same FK class
                    String fkClassName = getFkClassName(field.getName());
                    Class<?> fkClass = Class.forName("com.whats2watch.w2w.model." + fkClassName);

                    // Collect all related FK fields for this FK class
                    Map<String, Object> fkFieldValues = new HashMap<>();
                    for (Field relatedField : type.getDeclaredFields()) {
                        if (relatedField.getName().startsWith(fkClassName) && relatedField.isAnnotationPresent(ForeignKey.class)) {
                            String fkFieldName = extractFkFieldName(relatedField.getName());
                            fkFieldValues.put(fkFieldName, rs.getObject(relatedField.getName()));
                        }
                    }

                    // Fetch the FK entity using the collected values
                    Object fkEntity = fetchFKEntity(fkClass, fkFieldValues);
                    field.set(entity, fkEntity);
                } else {
                    // Set non-FK fields normally
                    field.set(entity, rs.getObject(field.getName()));
                }
            }

            return entity;
        } catch (Exception e) {
            throw new DAOException("Error mapping ResultSet to entity", e);
        }
    }

    private Object fetchFKEntity(Class<?> fkClass, Map<String, Object> fkFieldValues) throws Exception {
        if (fkClass.equals(Room.class)) {
            return createRoomWithFactory(fkFieldValues);
        } else if (fkClass.equals(Movie.class) || fkClass.equals(TVSeries.class)) {
            return createMediaWithFactory(fkClass, fkFieldValues);
        } else {
            // Default case: Use empty constructor
            Object fkEntity = fkClass.getDeclaredConstructor().newInstance();

            // Set primary key fields dynamically
            for (Field fkField : fkClass.getDeclaredFields()) {
                fkField.setAccessible(true);
                if (fkField.isAnnotationPresent(PrimaryKey.class)) {
                    Object value = fkFieldValues.get(fkField.getName());
                    if (value != null) {
                        fkField.set(fkEntity, value);
                    }
                }
            }
            return fkEntity;
        }
    }

    private Object createRoomWithFactory(Map<String, Object> fkFieldValues) throws Exception {
        Method factoryMethod = RoomFactory.class.getDeclaredMethod("createRoomInstance");
        Object builder = factoryMethod.invoke(null);

        populateBuilder(builder, fkFieldValues);

        Method buildMethod = builder.getClass().getMethod("build");
        return buildMethod.invoke(builder);
    }

    private Object createMediaWithFactory(Class<?> fkClass, Map<String, Object> fkFieldValues) throws Exception {
        String factoryMethodName = "create" + fkClass.getSimpleName() + "Instance";
        Method factoryMethod = MediaFactory.class.getDeclaredMethod(factoryMethodName);
        Object builder = factoryMethod.invoke(null);

        populateBuilder(builder, fkFieldValues);

        Method buildMethod = builder.getClass().getMethod("build");
        return buildMethod.invoke(builder);
    }

    private void populateBuilder(Object builder, Map<String, Object> fkFieldValues) throws Exception {
        for (Map.Entry<String, Object> entry : fkFieldValues.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            try {
                Method setterMethod = builder.getClass().getMethod(fieldName, fieldValue.getClass());
                setterMethod.invoke(builder, fieldValue);
            } catch (NoSuchMethodException ignored) {
                System.out.println("No setter method found for: " + fieldName);
            }
        }
    }

    private T createEntityInstance(Class<T> type) throws Exception {
        if (type.equals(Room.class)) {
            return (T) createRoomWithFactory(new HashMap<>()); // Assume no arguments for Room
        } else if (type.equals(Movie.class) || type.equals(TVSeries.class)) {
            return (T) createMediaWithFactory(type, new HashMap<>()); // Assume no arguments for Media
        } else {
            return type.getDeclaredConstructor().newInstance(); // Default case
        }
    }

    private String getFkClassName(String fieldName) {
        return fieldName.split("_")[0]; // Extracts FKClassName from FKClassName_FKFieldX_fk
    }

    private String extractFkFieldName(String fieldName) {
        return fieldName.split("_")[1]; // Extracts FKFieldX from FKClassName_FKFieldX_fk
    }

}
