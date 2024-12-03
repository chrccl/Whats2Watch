package com.whats2watch.w2w.model.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whats2watch.w2w.annotations.OneToMany;
import com.whats2watch.w2w.annotations.PrimaryKey;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class FileSystemDAO<T, K> implements DAO<T, K> {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemDAO.class);

    private final String baseDirectory;
    private final ObjectMapper objectMapper;
    private final Class<T> type;

    public FileSystemDAO(Class<T> type) {
        this.type = type;
        this.baseDirectory = FileSystemManager.getBaseDirectory();
        this.objectMapper = FileSystemManager.getObjectMapper();
    }

    @Override
    public void save(T entity) throws DAOException {
        handleOneToManySave(entity); // Handle relationships
        List<T> entities = readEntities();
        entities.add(entity);
        writeEntities(entities);
        logger.info("Entity saved: {}", entity);
    }

    @Override
    public T findById(Object entityId) throws DAOException {
        List<T> entities = readEntities();
        Map<String, Object> compositeKey = getCompositeKey(entityId);
        return entities.stream()
                .filter(e -> matchesCompositeKey(e, compositeKey))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Entity not found: " + compositeKey));
    }

    @Override
    public void deleteById(Object entityId) throws DAOException {
        List<T> entities = readEntities();
        Map<String, Object> compositeKey = getCompositeKey(entityId);
        boolean removed = entities.removeIf(e -> matchesCompositeKey(e, compositeKey));
        if (removed) {
            writeEntities(entities);
            logger.info("Entity deleted: {}", entityId);
        } else {
            throw new EntityNotFoundException("Entity not found for deletion: " + compositeKey);
        }
    }

    @Override
    public List<T> findAll() {
        try {
            return readEntities();
        } catch (DAOException e) {
            logger.error("Error reading all entities", e);
        }
        return List.of();
    }

    private Map<String, Object> getCompositeKey(Object entity) throws DAOException {
        Map<String, Object> compositeKey = new HashMap<>();
        try {
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(PrimaryKey.class)) {
                    compositeKey.put(field.getName(), field.get(entity));
                }
            }
        } catch (IllegalAccessException e) {
            logger.error("Error extracting composite key for entity: {}", entity, e);
            throw new DAOException("Error extracting composite key", e);
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
                logger.warn("Error matching composite key for entity: {}", entity, e);
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
            logger.error("Error reading entities from file", e);
            throw new DAOException("Error reading entities from file", e);
        }
    }

    private void writeEntities(List<T> entities) throws DAOException {
        try {
            objectMapper.writeValue(getFile(), entities);
        } catch (IOException e) {
            logger.error("Error writing entities to file", e);
            throw new DAOException("Error writing entities to file", e);
        }
    }

    private File getFile() {
        return new File(baseDirectory + type.getSimpleName().toLowerCase() + ".json");
    }

    private void handleOneToManySave(T entity) throws DAOException {
        try {
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(OneToMany.class)) {
                    List<?> relatedEntities = (List<?>) field.get(entity);
                    if (relatedEntities != null) {
                        for (Object relatedEntity : relatedEntities) {
                            saveRelatedEntity(relatedEntity);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            logger.error("Error handling one-to-many relationship for entity: {}", entity, e);
            throw new DAOException("Error handling one-to-many relationship", e);
        }
    }

    private void saveRelatedEntity(Object relatedEntity) throws DAOException {
        DatabaseDAO.saveEntity(relatedEntity, logger);
    }

}
