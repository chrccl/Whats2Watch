package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.annotations.*;
import com.whats2watch.w2w.exceptions.DAOException;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseDAO<T, K> implements DAO<T, K> {
    private final Class<T> type;
    private final Connection connection;
    private final String tableName;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseDAO.class);

    public DatabaseDAO(Class<T> type) throws DAOException {
        this.type = type;
        this.tableName = type.getSimpleName().toLowerCase();
        try {
            this.connection = ConnectionManager.getConnection();
        } catch (SQLException e) {
            throw new DAOException("Error establishing database connection", e);
        }
    }

    @Override
    public T findById(K k) {
        String query = String.format("SELECT * FROM %s WHERE k = ?", tableName);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, k);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    T entity = mapRowToEntity(rs);
                    loadRelationships(entity);
                    return entity;
                }
            }
        } catch (SQLException | DAOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        String query = String.format("SELECT * FROM %s", tableName);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                T entity = mapRowToEntity(rs);
                loadRelationships(entity);
                entities.add(entity);
            }
        } catch (SQLException | DAOException e) {
            logger.error(e.getMessage());
        }
        return entities;
    }

    @Override
    public void save(T entity) {
        try {
            Map<String, Object> fieldValues = getEntityFieldValues(entity);
            String sql = generateInsertSQL(fieldValues);

            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setPreparedStatementValues(stmt, fieldValues);
                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        setGeneratedId(entity, generatedKeys.getObject(1));
                    }
                }
            }

            saveRelationships(entity);
        } catch (SQLException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void deleteById(K k) {
        String query = String.format("DELETE FROM %s WHERE k = ?", tableName);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, k);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private T mapRowToEntity(ResultSet rs) throws SQLException, DAOException {
        try {
            T entity = type.getDeclaredConstructor().newInstance();
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                if (isNormalField(field)) {
                    field.set(entity, rs.getObject(field.getName()));
                }
            }
            return entity;
        } catch (Exception e) {
            String errorMessage = String.format("Error mapping row to entity of type %s", type.getName());
            logger.error(errorMessage, e);
            throw new DAOException(errorMessage, e);
        }
    }

    private Map<String, Object> getEntityFieldValues(T entity) throws IllegalAccessException {
        Map<String, Object> fieldValues = new LinkedHashMap<>();
        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            if (isNormalField(field)) {
                fieldValues.put(field.getName(), field.get(entity));
            }
        }
        return fieldValues;
    }

    private String generateInsertSQL(Map<String, Object> fieldValues) {
        String columns = String.join(",", fieldValues.keySet());
        String placeholders = String.join(",", Collections.nCopies(fieldValues.size(), "?"));
        return "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
    }

    private void setPreparedStatementValues(PreparedStatement stmt, Map<String, Object> fieldValues) throws SQLException {
        int index = 1;
        for (Object value : fieldValues.values()) {
            stmt.setObject(index++, value);
        }
    }

    private void setGeneratedId(T entity, Object id) throws IllegalAccessException {
        for (Field field : type.getDeclaredFields()) {
            if (isPrimaryKey(field)) {
                field.setAccessible(true);
                field.set(entity, id);
                break;
            }
        }
    }

    private boolean isPrimaryKey(Field field) {
        return field.isAnnotationPresent(PrimaryKey.class);
    }

    private boolean isNormalField(Field field) {
        return !(field.isAnnotationPresent(OneToOne.class)
                || field.isAnnotationPresent(OneToMany.class)
                || field.isAnnotationPresent(ManyToOne.class)
                || field.isAnnotationPresent(ManyToMany.class));
    }

    private void loadRelationships(T entity) {
        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(OneToOne.class)) {
                handleOneToOneLoad(entity, field);
            } else if (field.isAnnotationPresent(OneToMany.class)) {
                handleOneToManyLoad(entity, field);
            } else if (field.isAnnotationPresent(ManyToOne.class)) {
                handleManyToOneLoad(entity, field);
            } else if (field.isAnnotationPresent(ManyToMany.class)) {
                handleManyToManyLoad(entity, field);
            }
        }
    }

    private void saveRelationships(T entity) {
        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(OneToOne.class)) {
                handleOneToOneSave(entity, field);
            } else if (field.isAnnotationPresent(OneToMany.class)) {
                handleOneToManySave(entity, field);
            } else if (field.isAnnotationPresent(ManyToMany.class)) {
                handleManyToManySave(entity, field);
            }
        }
    }

    private void handleOneToOneLoad(T entity, Field field) {
        try {
            OneToOne oneToOne = field.getAnnotation(OneToOne.class);
            Class<?> targetEntity = oneToOne.targetEntity();

            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            Object idValue = idField.get(entity);

            fetchFKEntity(entity, field, targetEntity, idValue);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void fetchFKEntity(T entity, Field field, Class<?> targetEntity, Object idValue) throws SQLException, IllegalAccessException {
        String query = String.format("SELECT * FROM %s WHERE id = ?", targetEntity.getSimpleName().toLowerCase());
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, idValue);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Object relatedEntity = mapRowToEntity(rs, targetEntity);
                    field.set(entity, relatedEntity);
                }
            }
        }
    }

    private void handleOneToOneSave(T entity, Field field) {
        try {
            field.setAccessible(true);
            Object relatedEntity = field.get(entity);

            if (relatedEntity != null) {
                saveRelatedEntity(relatedEntity);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void handleOneToManyLoad(T entity, Field field) {
        try {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            Class<?> targetEntity = oneToMany.targetEntity();
            String mappedBy = oneToMany.mappedBy();

            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            Object idValue = idField.get(entity);

            String query = String.format("SELECT * FROM %s WHERE %s = ?", targetEntity.getSimpleName().toLowerCase(), mappedBy);
            prepareGenericStatement(entity, field, targetEntity, idValue, query);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void handleOneToManySave(T entity, Field field) {
        try {
            field.setAccessible(true);
            List<?> relatedEntities = (List<?>) field.get(entity);

            if (relatedEntities != null) {
                for (Object relatedEntity : relatedEntities) {
                    String mappedBy = field.getAnnotation(OneToMany.class).mappedBy();
                    if (!mappedBy.isEmpty()) {
                        Field parentField = relatedEntity.getClass().getDeclaredField(mappedBy);
                        parentField.setAccessible(true);
                        parentField.set(relatedEntity, entity);
                    }

                    saveRelatedEntity(relatedEntity);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void handleManyToOneLoad(T entity, Field field) {
        try {
            ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
            Class<?> targetEntity = manyToOne.targetEntity();

            field.setAccessible(true);
            Object foreignKeyValue = field.get(entity);

            if (foreignKeyValue != null) {
                fetchFKEntity(entity, field, targetEntity, foreignKeyValue);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void handleManyToManyLoad(T entity, Field field) {
        try {
            ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            Class<?> targetEntity = manyToMany.targetEntity();
            String joinTable = manyToMany.joinTable();
            String joinColumn = manyToMany.joinColumn();
            String inverseJoinColumn = manyToMany.inverseJoinColumn();

            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            Object idValue = idField.get(entity);

            String query = String.format("SELECT * FROM %s jt JOIN %s te ON jt.%s = te.id WHERE jt.%s = ?",
                    joinTable, targetEntity.getSimpleName().toLowerCase(), inverseJoinColumn, joinColumn);
            prepareGenericStatement(entity, field, targetEntity, idValue, query);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void prepareGenericStatement(T entity, Field field, Class<?> targetEntity, Object idValue, String query) throws SQLException, IllegalAccessException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, idValue);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Object> relatedEntities = new ArrayList<>();
                while (rs.next()) {
                    relatedEntities.add(mapRowToEntity(rs, targetEntity));
                }
                field.set(entity, relatedEntities);
            }
        }
    }

    private void handleManyToManySave(T entity, Field field) {
        try {
            field.setAccessible(true);
            List<?> relatedEntities = (List<?>) field.get(entity);

            if (relatedEntities != null && !relatedEntities.isEmpty()) {
                ManyToMany annotation = field.getAnnotation(ManyToMany.class);
                String joinTable = annotation.joinTable();
                String joinColumn = annotation.joinColumn();
                String inverseJoinColumn = annotation.inverseJoinColumn();

                for (Object relatedEntity : relatedEntities) {
                    saveRelatedEntity(relatedEntity);

                    String insertSQL = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?)",
                            joinTable, joinColumn, inverseJoinColumn);
                    try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
                        stmt.setObject(1, getPrimaryKeyValue(entity));
                        stmt.setObject(2, getPrimaryKeyValue(relatedEntity));
                        stmt.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private Object getPrimaryKeyValue(Object entity) throws IllegalAccessException {
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                field.setAccessible(true);
                return field.get(entity);
            }
        }
        throw new IllegalArgumentException("Entity has no primary key field");
    }

    private Object mapRowToEntity(ResultSet rs, Class<?> targetType) throws SQLException {
        try {
            Object entity = targetType.getDeclaredConstructor().newInstance();
            for (Field field : targetType.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(entity, rs.getObject(field.getName()));
            }
            return entity;
        } catch (Exception e) {
            throw new SQLException("Failed to map row to entity", e);
        }
    }

    private void saveRelatedEntity(Object relatedEntity) throws DAOException {
        saveEntity(relatedEntity, logger);
    }

    static void saveEntity(Object relatedEntity, Logger logger) throws DAOException {
        try {
            @SuppressWarnings("unchecked")
            FileSystemDAO<Object, Object> relatedDAO = new FileSystemDAO<>((Class<Object>) relatedEntity.getClass());
            relatedDAO.save(relatedEntity);
        } catch (Exception e) {
            String errorMessage = String.format("Error saveRelatedEntity of type: %s", relatedEntity.getClass());
            logger.error(errorMessage, e);
            throw new DAOException(errorMessage, e);
        }
    }

}
