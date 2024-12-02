package com.whats2watch.w2w.model.dao;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    public Boolean save(T entity) {
        try {
            String fileName = getFileName();
            File file = new File(baseDirectory + fileName);

            List<T> entities = new ArrayList<>();
            if (file.exists()) {
                entities = readEntitiesFromFile(file);
            }

            entities.add(entity);

            objectMapper.writeValue(file, entities);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving entity to file system: " + e.getMessage());
            return false;
        }
    }

    @Override
    public T findById(Map<String, Object> compositeKey) {
        try {
            String fileName = getFileName();
            File file = new File(baseDirectory + fileName);

            if (!file.exists()) {
                return null;  // File does not exist
            }

            List<T> entities = readEntitiesFromFile(file);

            for (T entity : entities) {
                if (matchesCompositeKey(entity, compositeKey)) {
                    return entity;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from file system: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Boolean delete(Map<String, Object> compositeKey) {
        try {
            String fileName = getFileName();
            File file = new File(baseDirectory + fileName);

            if (!file.exists()) {
                return false;  // File does not exist
            }

            List<T> entities = readEntitiesFromFile(file);
            List<T> filteredEntities = entities.stream()
                    .filter(entity -> !matchesCompositeKey(entity, compositeKey))
                    .collect(Collectors.toList());

            if (filteredEntities.size() < entities.size()) {
                objectMapper.writeValue(file, filteredEntities);
                return true;
            }

        } catch (IOException e) {
            System.out.println("Error deleting from file system: " + e.getMessage());
        }
        return false;
    }

    private List<T> readEntitiesFromFile(File file) throws IOException {
        return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
    }

    private String getFileName() {
        return type.getSimpleName().toLowerCase() + ".json";
    }

    private boolean matchesCompositeKey(T entity, Map<String, Object> compositeKey) {
        // Check if the entity matches the composite key
        for (Map.Entry<String, Object> entry : compositeKey.entrySet()) {
            try {
                var field = type.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                Object fieldValue = field.get(entity);
                if (!Objects.equals(fieldValue, entry.getValue())) {
                    return false;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return false;
            }
        }
        return true;
    }
}
