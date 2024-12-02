package com.whats2watch.w2w.model.dao;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.exceptions.EntityNotFoundException;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileSystemDAO<T> implements GenericDAO<T> {
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
        try {
            String fileName = getFileName();
            File file = new File(baseDirectory + fileName);
            List<T> entities = file.exists() ? readEntitiesFromFile(file) : new ArrayList<>();

            entities.add(entity);
            objectMapper.writeValue(file, entities);
            return true;
        } catch (IOException e) {
            throw new DAOException("Error saving entity to file system", e);
        }
    }

    @Override
    public T findById(Map<String, Object> compositeKey) throws DAOException {
        try {
            String fileName = getFileName();
            File file = new File(baseDirectory + fileName);

            if (!file.exists()) {
                throw new EntityNotFoundException("Entity file not found: " + fileName);
            }

            List<T> entities = readEntitiesFromFile(file);
            return entities.stream()
                    .filter(entity -> matchesCompositeKey(entity, compositeKey))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Entity not found for given key: " + compositeKey));
        } catch (IOException e) {
            throw new DAOException("Error reading from file system", e);
        }
    }

    @Override
    public Boolean delete(Map<String, Object> compositeKey) throws DAOException {
        try {
            String fileName = getFileName();
            File file = new File(baseDirectory + fileName);

            if (!file.exists()) {
                throw new EntityNotFoundException("Entity file not found: " + fileName);
            }

            List<T> entities = readEntitiesFromFile(file);
            List<T> filteredEntities = entities.stream()
                    .filter(entity -> !matchesCompositeKey(entity, compositeKey))
                    .collect(Collectors.toList());

            if (filteredEntities.size() < entities.size()) {
                objectMapper.writeValue(file, filteredEntities);
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new DAOException("Error deleting entity from file system", e);
        }
    }

    private List<T> readEntitiesFromFile(File file) throws IOException {
        return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
    }

    private String getFileName() {
        return type.getSimpleName().toLowerCase() + ".json";
    }

    private boolean matchesCompositeKey(T entity, Map<String, Object> compositeKey) {
        for (Map.Entry<String, Object> entry : compositeKey.entrySet()) {
            try {
                var field = type.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                if (!Objects.equals(field.get(entity), entry.getValue())) {
                    return false;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return false;
            }
        }
        return true;
    }
}
