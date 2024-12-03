package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.annotations.ForeignKey;
import com.whats2watch.w2w.annotations.PrimaryKey;
import com.whats2watch.w2w.exceptions.DAOException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.*;

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

            // Handle collection FKs for join table insertions
            saveCollectionFKs(entity);

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new DAOException("Error while saving entity to DB", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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
                T entity = mapResultSetToEntity(rs);

                // Handle collections of foreign keys
                populateCollectionFKs(entity);

                return entity;
            }
        } catch (SQLException | ClassNotFoundException | IllegalAccessException e) {
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
                } else {
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
            Object fkEntity = field.get(entity);
            if (fkEntity == null) {
                throw new DAOException("Foreign key field cannot be null: " + field.getName());
            }

            for (Field fkField : fkEntity.getClass().getDeclaredFields()) {
                fkField.setAccessible(true);

                if (fkField.isAnnotationPresent(PrimaryKey.class)) {
                    String fkColumnName = field.getName() + "_fk";
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
                    if (isCollectionField(field)) {
                        // Handle collection of FK entities
                        String joinTableName = getJoinTableName(type, field);
                        Class<?> fkClass = getCollectionGenericType(field);
                        Collection<Object> fkEntities = fetchFKEntitiesFromJoinTable(entity, joinTableName, fkClass);
                        field.set(entity, fkEntities);
                    } else {
                        // Handle single FK entity
                        handleSingleFKField(rs, entity, field);
                    }
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

    private void saveCollectionFKs(T entity) throws SQLException, DAOException, ClassNotFoundException {
        // Iterate over the declared fields of the entity
        for (Field field : type.getDeclaredFields()) {
            if (isCollectionField(field) && field.isAnnotationPresent(ForeignKey.class)) {
                // Get the join table name and FK class type
                String joinTableName = getJoinTableName(type, field);
                Class<?> fkClass = getCollectionGenericType(field);

                // Make the field accessible and retrieve the collection
                field.setAccessible(true);
                Collection<?> fkEntities;
                try {
                    fkEntities = (Collection<?>) field.get(entity);
                } catch (IllegalAccessException e) {
                    throw new DAOException("Error accessing collection field: " + field.getName(), e);
                }

                // Iterate over the collection of foreign key entities
                for (Object fkEntity : fkEntities) {
                    insertIntoJoinTable(joinTableName, entity, fkEntity, fkClass);
                }
            }
        }
    }

    private void insertIntoJoinTable(String joinTableName, T entity, Object fkEntity, Class<?> fkClass) throws SQLException, DAOException {
        // Construct the SQL query to insert the primary entity and FK entity into the join table
        String sql = String.format("INSERT INTO %s (primary_id, fk_id) VALUES (?, ?)", joinTableName);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Set the primary key value of the entity
            stmt.setObject(1, getPrimaryKeyValue(entity));

            // Set the primary key value of the foreign key entity
            stmt.setObject(2, getPrimaryKeyValue(fkEntity));

            // Execute the insert statement
            stmt.executeUpdate();
        }
    }

    private void populateCollectionFKs(T entity) throws SQLException, DAOException, ClassNotFoundException, IllegalAccessException {
        for (Field field : type.getDeclaredFields()) {
            if (isCollectionField(field) && field.isAnnotationPresent(ForeignKey.class)) {
                String joinTableName = getJoinTableName(type, field);
                Class<?> fkClass = getCollectionGenericType(field);
                Collection<Object> fkEntities = fetchFKEntitiesFromJoinTable(entity, joinTableName, fkClass);
                field.set(entity, fkEntities);
            }
        }
    }

    private Collection<Object> fetchFKEntitiesFromJoinTable(T entity, String joinTableName, Class<?> fkClass) throws SQLException, DAOException {
        Collection<Object> fkEntities = new ArrayList<>();
        String primaryKeyColumn = getPrimaryKeyColumn(type);
        Object primaryKeyValue = getPrimaryKeyValue(entity);

        String query = String.format("SELECT fk_id FROM %s WHERE primary_id = ?", joinTableName);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, primaryKeyValue);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Object fkEntityId = resultSet.getObject("fk_id");
                Object fkEntity = fetchFKEntity(fkClass, Map.of(getPrimaryKeyColumn(fkClass), fkEntityId));
                fkEntities.add(fkEntity);
            }
        } catch (Exception e) {
            throw new DAOException("Error fetching fk entities from join table", e);
        }
        return fkEntities;
    }

    private Object fetchFKEntity(Class<?> fkClass, Map<String, Object> fkFieldValues) throws Exception {
        Object fkEntity = fkClass.getDeclaredConstructor().newInstance();

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

    private T createEntityInstance(Class<T> type) throws Exception {
        return type.getDeclaredConstructor().newInstance();
    }

    private boolean isCollectionField(Field field) {
        return Collection.class.isAssignableFrom(field.getType());
    }

    private String getJoinTableName(Class<?> primaryClass, Field fkField) {
        return primaryClass.getSimpleName().toLowerCase() + "_" + fkField.getName().toLowerCase();
    }

    private Class<?> getCollectionGenericType(Field field) throws ClassNotFoundException {
        String typeName = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
        return Class.forName(typeName);
    }

    private void handleSingleFKField(ResultSet rs, T entity, Field field) throws Exception {
        String fkClassName = getFkClassName(field.getName());
        Class<?> fkClass = Class.forName("com.whats2watch.w2w.model." + fkClassName);

        Map<String, Object> fkFieldValues = new HashMap<>();
        for (Field relatedField : type.getDeclaredFields()) {
            if (relatedField.getName().startsWith(fkClassName) && relatedField.isAnnotationPresent(ForeignKey.class)) {
                String fkFieldName = extractFkFieldName(relatedField.getName());
                fkFieldValues.put(fkFieldName, rs.getObject(relatedField.getName()));
            }
        }

        Object fkEntity = fetchFKEntity(fkClass, fkFieldValues);
        field.set(entity, fkEntity);
    }

    private Object getPrimaryKeyValue(Object entity) throws DAOException {
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                try {
                    return field.get(entity);
                } catch (IllegalAccessException e) {
                    throw new DAOException("Error accessing primary key field", e);
                }
            }
        }
        throw new DAOException("No primary key found for entity: " + entity.getClass().getName());
    }

    // Method to extract the foreign key class name from a field's name
    private String getFkClassName(String fieldName) {
        // Assume the naming convention is FKClassName_FKFieldName_fk
        // We extract the class name (e.g., "Movie" from "Movie_title_fk")
        return fieldName.split("_")[0];
    }

    // Method to retrieve the name of the primary key field in a given class
    private String getPrimaryKeyColumn(Class<?> cls) {
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                return field.getName(); // Return the name of the primary key field
            }
        }
        throw new IllegalArgumentException("No primary key found in class: " + cls.getName());
    }

    // Method to extract the foreign key field name from a field's name (i.e., FKFieldName from FKClassName_FKFieldName_fk)
    private String extractFkFieldName(String fieldName) {
        // Assuming the format is FKClassName_FKFieldName_fk, we extract the FK field name
        String[] parts = fieldName.split("_");
        if (parts.length > 1) {
            return parts[1]; // Returns the FK field name (e.g., "title" from "Movie_title_fk")
        }
        throw new IllegalArgumentException("Invalid foreign key field name: " + fieldName);
    }

}
