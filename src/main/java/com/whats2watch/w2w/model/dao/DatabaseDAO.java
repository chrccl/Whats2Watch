package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.annotations.ForeignKey;
import com.whats2watch.w2w.annotations.PrimaryKey;

import com.whats2watch.w2w.exceptions.DAOException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;

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
        String sql = buildDeleteQuery(tableName, entityId);

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

    private String buildDeleteQuery(String tableName, T entityId) throws DAOException {
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
            T entity = type.getDeclaredConstructor().newInstance();

            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);

                if (field.isAnnotationPresent(ForeignKey.class)) {
                    // Handle foreign key fields
                    String fkColumnName = field.getName();
                    Object fkValue = rs.getObject(fkColumnName);

                    if (fkValue != null) {
                        // Extract FK entity class from field name that has this syntax: "FKClassName_FKPKFieldName_fk"
                        String fkEntityClassName = field.getName().substring(0, field.getName().indexOf("_"));
                        Class<?> fkEntityClass = Class.forName("com.whats2watch.w2w.model." + fkEntityClassName);

                        // Fetch the related entity using its primary key (which is fkValue)
                        Object fkEntity = getEntityByPrimaryKey(fkEntityClass, fkValue);

                        // Set the related entity in the current entity's foreign key field
                        field.set(entity, fkEntity);
                    }
                } else {
                    // Set other fields as usual
                    field.set(entity, rs.getObject(field.getName()));
                }
            }
            return entity;
        } catch (Exception e) {
            throw new DAOException("Error mapping ResultSet to entity", e);
        }
    }

    private Object getEntityByPrimaryKey(Class<?> fkEntityClass, Object fkValue) throws DAOException {
        try {
            // Assuming you have a DAO for each entity
            DAO<?> fkDao = getDaoForEntity(fkEntityClass);

            // Create a temporary entity with the primary key value set
            Object fkEntity = fkEntityClass.getDeclaredConstructor().newInstance();

            // Set the primary key on the entity
            for (Field field : fkEntityClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    field.set(fkEntity, fkValue);
                }
            }

            // Use reflection to invoke the correct findById method
            Method findByIdMethod = fkDao.getClass().getMethod("findById", fkEntityClass); // Dynamically get the method
            return findByIdMethod.invoke(fkDao, fkEntity);
        } catch (Exception e) {
            throw new DAOException("Error retrieving foreign key entity by primary key", e);
        }
    }

    private DAO<?> getDaoForEntity(Class<?> fkEntityClass) throws DAOException {
        try {
            String daoClassName = fkEntityClass.getPackage().getName() + ".dao." + fkEntityClass.getSimpleName() + "DAO";
            Class<?> daoClass = Class.forName(daoClassName);

            if (DAO.class.isAssignableFrom(daoClass)) {
                return createDaoInstance(daoClass, fkEntityClass);
            } else {
                throw new DAOException("DAO class does not implement DAO interface: " + daoClassName);
            }
        } catch (ClassNotFoundException e) {
            throw new DAOException("Error while creating DAO for entity: " + fkEntityClass.getSimpleName(), e);
        }
    }

    private DAO<?> createDaoInstance(Class<?> daoClass, Class<?> fkEntityClass) throws DAOException {
        try {
            Constructor<?> constructor = daoClass.getConstructor(Class.class);
            return (DAO<?>) constructor.newInstance(fkEntityClass);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DAOException("Error creating DAO instance: " + daoClass.getSimpleName(), e);
        }
    }
}
