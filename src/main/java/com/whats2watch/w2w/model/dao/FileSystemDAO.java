package com.whats2watch.w2w.model.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whats2watch.w2w.annotations.PrimaryKey;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.exceptions.EntityNotFoundException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class FileSystemDAO<T> implements DAO<T> {

    private final String baseDirectory;
    private final ObjectMapper objectMapper;
    private final Class<T> type;

    public FileSystemDAO(Class<T> type) {
        this.type = type;
        this.baseDirectory = FileSystemManager.getBaseDirectory();
        this.objectMapper = FileSystemManager.getObjectMapper();
    }

    @Override
    public Boolean save(T entity) throws DAOException {
        List<T> entities = readEntities();
        entities.add(entity);
        writeEntities(entities);
        return true;
    }

    @Override
    public T findById(T entityId) throws DAOException {
        List<T> entities = readEntities();
        Map<String, Object> compositeKey = getCompositeKey(entityId);
        return entities.stream()
                .filter(e -> matchesCompositeKey(e, compositeKey))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Entity not found: " + compositeKey));
    }

    @Override
    public Boolean deleteById(T entityId) throws DAOException {
        List<T> entities = readEntities();
        Map<String, Object> compositeKey = getCompositeKey(entityId);
        boolean removed = entities.removeIf(e -> matchesCompositeKey(e, compositeKey));
        if (removed) {
            writeEntities(entities);
        }
        return removed;
    }

    @Override
    public Boolean updateById(T entityId) throws DAOException {
        List<T> entities = readEntities();
        Map<String, Object> compositeKey = getCompositeKey(entityId);

        for (int i = 0; i < entities.size(); i++) {
            if (matchesCompositeKey(entities.get(i), compositeKey)) {
                updateEntity(entities.get(i), entityId);
                writeEntities(entities);
                return true;
            }
        }
        return false;
    }

    private void updateEntity(T existingEntity, T newEntity) throws DAOException {
        try {
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(PrimaryKey.class)) {
                    Object newValue = field.get(newEntity);
                    if (newValue != null) {
                        field.set(existingEntity, newValue);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new DAOException("Error updating entity", e);
        }
    }

    private Map<String, Object> getCompositeKey(T entity) throws DAOException {
        Map<String, Object> compositeKey = new HashMap<>();
        try {
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    compositeKey.put(field.getName(), field.get(entity));
                }
            }
        } catch (IllegalAccessException e) {
            throw new DAOException("Error extracting primary key", e);
        }
        return compositeKey;
    }

    private boolean matchesCompositeKey(T entity, Map<String, Object> compositeKey) {
        return compositeKey.entrySet().stream().allMatch(entry -> {
            try {
                Field field = type.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                return Objects.equals(field.get(entity), entry.getValue());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return false;
            }
        });
    }

    private List<T> readEntities() throws DAOException {
        File file = getFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
        } catch (IOException e) {
            throw new DAOException("Error reading entities from file", e);
        }
    }

    private void writeEntities(List<T> entities) throws DAOException {
        try {
            objectMapper.writeValue(getFile(), entities);
        } catch (IOException e) {
            throw new DAOException("Error writing entities to file", e);
        }
    }

    private File getFile() {
        return new File(baseDirectory + type.getSimpleName().toLowerCase() + ".json");
    }
}
