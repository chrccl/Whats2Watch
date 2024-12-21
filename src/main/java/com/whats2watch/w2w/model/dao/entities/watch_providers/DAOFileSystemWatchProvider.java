package com.whats2watch.w2w.model.dao.entities.watch_providers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.WatchProvider;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DAOFileSystemWatchProvider implements DAO<WatchProvider, String> {

    private final String filePath;
    private final Map<String, WatchProvider> watchProviderStorage;
    private final Gson gson;

    public DAOFileSystemWatchProvider(String filePath) throws DAOException {
        this.filePath = filePath;
        this.watchProviderStorage = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadFromFile();
    }

    @Override
    public void save(WatchProvider entity) throws DAOException {
        watchProviderStorage.put(entity.getProviderName(), entity);
        saveToFile();
    }

    @Override
    public WatchProvider findById(String entityKey) throws DAOException {
        WatchProvider entity = watchProviderStorage.get(entityKey);
        if (entity == null) {
            throw new DAOException("Watch provider not found with key: " + entityKey);
        }
        return entity;
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        if (watchProviderStorage.remove(entityKey) == null) {
            throw new DAOException("Watch Provider not found with key: " + entityKey);
        }
        saveToFile();
    }

    @Override
    public Set<WatchProvider> findAll() throws DAOException {
        return  new HashSet<>(watchProviderStorage.values());
    }

    protected void loadFromFile() throws DAOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (Reader reader = new FileReader(file)) {
            WatchProvider[] entities = gson.fromJson(reader, WatchProvider[].class);
            for (WatchProvider entity : entities) {
                watchProviderStorage.put(entity.getProviderName(), entity);
            }
        } catch (IOException e) {
            throw new DAOException("Failed to load data from file: " + e.getMessage(), e);
        }
    }

    protected void saveToFile() throws DAOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(watchProviderStorage.values(), writer);
        } catch (IOException e) {
            throw new DAOException("Failed to save data to file: " + e.getMessage(), e);
        }
    }

}
