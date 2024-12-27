package com.whats2watch.w2w.model.dao.entities.media;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Media;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class DAOFileSystemMedia<T extends Media> implements DAO<T, MediaId> {

    protected final String filePath;
    protected final Map<MediaId, T> storage;
    protected final Gson gson;
    protected final Type type;

    protected DAOFileSystemMedia(String filePath, Type type) throws DAOException {
        this.filePath = filePath;
        this.storage = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.type = type;
        loadFromFile();
    }

    @Override
    public void save(T entity) throws DAOException {
        storage.put(entity.getMediaId(), entity);
        saveToFile();
    }

    @Override
    public T findById(MediaId entityKey) throws DAOException {
        T entity = storage.get(entityKey);
        if (entity == null) {
            throw new DAOException(getEntityName() + " not found with key: " + entityKey);
        }
        return entity;
    }

    @Override
    public void deleteById(MediaId entityKey) throws DAOException {
        if (storage.remove(entityKey) == null) {
            throw new DAOException(getEntityName() + " not found with key: " + entityKey);
        }
        saveToFile();
    }

    @Override
    public Set<T> findAll() throws DAOException {
        return storage.entrySet()
                .stream()
                .sorted((entry1, entry2) ->
                        Integer.compare(entry2.getKey().getYear(), entry1.getKey().getYear())) // Sort by year in descending order
                .map(Map.Entry::getValue) // Extract the values from the entries
                .collect(Collectors.toCollection(LinkedHashSet::new)); // Collect the values into a list
    }

    protected void loadFromFile() throws DAOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (Reader reader = new FileReader(file)) {
            T[] entities = gson.fromJson(reader, type);
            for (T entity : entities) {
                storage.put(entity.getMediaId(), entity);
            }
        } catch (IOException e) {
            throw new DAOException("Failed to load data from file: " + e.getMessage(), e);
        }
    }

    protected void saveToFile() throws DAOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(storage.values(), writer);
        } catch (IOException e) {
            throw new DAOException("Failed to save data to file: " + e.getMessage(), e);
        }
    }

    protected abstract String getEntityName();
}
