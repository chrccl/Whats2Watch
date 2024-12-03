package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.annotations.ForeignKey;
import com.whats2watch.w2w.annotations.PrimaryKey;
import com.whats2watch.w2w.exceptions.DAOException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    private void saveCollectionFKs(T entity) throws SQLException, DAOException {
        // Iterate over the declared fields of the entity
        for (Field field : type.getDeclaredFields()) {
            if (isCollectionField(field) && field.isAnnotationPresent(ForeignKey.class)) {
                // Get the join table name and FK class type
                String joinTableName = getJoinTableName(type, field);

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
                    insertIntoJoinTable(joinTableName, entity, fkEntity);
                }
            }
        }
    }

    private void populateCollectionFKs(T entity) throws DAOException, ClassNotFoundException, IllegalAccessException {
        // Iterate over the fields of the entity
        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);

            // Check if the field is a collection and is annotated with ForeignKey
            if (isCollectionField(field) && field.isAnnotationPresent(ForeignKey.class)) {
                String joinTableName = getJoinTableName(type, field);  // Get the join table name
                Class<?> fkClass = getCollectionGenericType(field);    // Get the class of the foreign key entity

                // Fetch and set the foreign key entities into the collection
                Collection<Object> fkEntities = fetchFKEntitiesFromJoinTable(entity, joinTableName, fkClass);
                field.set(entity, fkEntities); // Populate the collection field with the fetched foreign key entities
            }
        }
    }


    private void insertIntoJoinTable(String joinTableName, T entity, Object fkEntity) throws SQLException, DAOException {
        // Get the primary key columns and values
        List<String> pkColumns = getPrimaryKeyColumns(type);
        List<Object> pkValues = new ArrayList<>();
        for (String pkColumn : pkColumns) {
            pkValues.add(getPrimaryKeyValue(entity, pkColumn));
        }

        // Get the foreign key columns and values
        List<String> fkColumns = getPrimaryKeyColumns(fkEntity.getClass()); // Assuming the foreign key entity also has composite primary key
        List<Object> fkValues = new ArrayList<>();
        for (String fkColumn : fkColumns) {
            fkValues.add(getPrimaryKeyValue(fkEntity, fkColumn));
        }

        // Generate the SQL query
        String columns = generateJoinTableColumns(pkColumns, fkColumns);
        String values = generateJoinTableValues(pkValues, fkValues);

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", joinTableName, columns, values);

        // Execute the query
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int index = 1;

            // Set the primary key values
            for (Object pkValue : pkValues) {
                stmt.setObject(index++, pkValue);
            }

            // Set the foreign key values
            for (Object fkValue : fkValues) {
                stmt.setObject(index++, fkValue);
            }

            stmt.executeUpdate();
        }
    }

    private String generateJoinTableColumns(List<String> pkColumns, List<String> fkColumns) {
        StringBuilder columns = new StringBuilder();

        // Add primary key columns
        for (String pkColumn : pkColumns) {
            columns.append(pkColumn).append(", ");
        }

        // Add foreign key columns
        for (String fkColumn : fkColumns) {
            columns.append(fkColumn).append(", ");
        }

        // Remove the trailing comma and space
        columns.delete(columns.length() - 2, columns.length());

        return columns.toString();
    }

    private String generateJoinTableValues(List<Object> pkValues, List<Object> fkValues) {
        StringBuilder values = new StringBuilder();

        // Add primary key values
        for (Object pkValue : pkValues) {
            values.append("?, ");
        }

        // Add foreign key values
        for (Object fkValue : fkValues) {
            values.append("?, ");
        }

        // Remove the trailing comma and space
        values.delete(values.length() - 2, values.length());

        return values.toString();
    }
    private Collection<Object> fetchFKEntitiesFromJoinTable(T entity, String joinTableName, Class<?> fkClass) throws DAOException {
        Collection<Object> fkEntities = new ArrayList<>();

        // Get the primary key values from the entity
        Map<String, Object> primaryKeyValues = getPrimaryKeyValues(entity); // Fetch primary key values for the main entity

        // Build the WHERE clause for the join table query (e.g., WHERE roomId = ? AND locationId = ?)
        StringBuilder whereClause = new StringBuilder();
        for (String pkColumn : primaryKeyValues.keySet()) {
            whereClause.append(pkColumn).append(" = ? AND ");
        }
        whereClause.delete(whereClause.length() - 5, whereClause.length()); // Remove trailing " AND "

        String query = String.format("SELECT fk_id FROM %s WHERE %s", joinTableName, whereClause.toString());
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Set the primary key values as parameters for the WHERE clause
            int index = 1;
            for (Object pkValue : primaryKeyValues.values()) {
                stmt.setObject(index++, pkValue);
            }

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Object fkEntityId = resultSet.getObject("fk_id"); // Get the foreign key ID from the join table
                Object fkEntity = fetchFKEntity(fkClass, Map.of(getPrimaryKeyColumn(fkClass), fkEntityId));
                fkEntities.add(fkEntity); // Add the foreign key entity to the collection
            }
        } catch (SQLException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException | InstantiationException e) {
            throw new DAOException("Error fetching fk entities from join table", e);
        }
        return fkEntities;
    }

    private Object fetchFKEntity(Class<?> fkClass, Map<String, Object> fkFieldValues) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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

    private T createEntityInstance(Class<T> type) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
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

    private void handleSingleFKField(ResultSet rs, T entity, Field field) throws ClassNotFoundException, SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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

    private Object getPrimaryKeyValue(Object entity, String pkColumn) throws DAOException {
        try {
            Field field = entity.getClass().getDeclaredField(pkColumn); // Access the specific primary key field
            field.setAccessible(true);
            return field.get(entity); // Return the value of that specific primary key field
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new DAOException("Error accessing primary key field: " + pkColumn, e);
        }
    }

    private Map<String, Object> getPrimaryKeyValues(Object entity) throws DAOException {
        Map<String, Object> pkValues = new HashMap<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                try {
                    pkValues.put(field.getName(), field.get(entity)); // Add primary key field value to map
                } catch (IllegalAccessException e) {
                    throw new DAOException("Error accessing primary key field: " + field.getName(), e);
                }
            }
        }
        if (pkValues.isEmpty()) {
            throw new DAOException("No primary key found for entity: " + entity.getClass().getName());
        }
        return pkValues; // Return a map of primary key field names and their values
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

    private List<String> getPrimaryKeyColumns(Class<?> cls) {
        List<String> pkColumns = new ArrayList<>();
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                pkColumns.add(field.getName()); // Add the name of the primary key field
            }
        }
        if (pkColumns.isEmpty()) {
            throw new IllegalArgumentException("No primary key found in class: " + cls.getName());
        }
        return pkColumns;
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
