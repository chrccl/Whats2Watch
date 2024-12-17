package com.whats2watch.w2w.model.dao.entities.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DAOFileSystemUser implements DAO<User, String> {

    private final String filePath;
    private final Map<String, User> userStorage;
    private final Gson gson;

    public DAOFileSystemUser(String filePath) throws DAOException {
        this.filePath = filePath;
        this.userStorage = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadFromFile();
    }

    @Override
    public void save(User entity) throws DAOException {
        userStorage.put(entity.getEmail(), entity);
        saveToFile();
    }

    @Override
    public User findById(String entityKey) throws DAOException {
        User entity = userStorage.get(entityKey);
        if (entity == null) {
            throw new DAOException("User not found with key: " + entityKey);
        }
        return entity;
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        if (userStorage.remove(entityKey) == null) {
            throw new DAOException("User not found with key: " + entityKey);
        }
        saveToFile();
    }

    @Override
    public Set<User> findAll() throws DAOException {
        return new HashSet<>(userStorage.values());
    }

    protected void loadFromFile() throws DAOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (Reader reader = new FileReader(file)) {
            User[] entities = gson.fromJson(reader, User[].class);
            for (User entity : entities) {
                userStorage.put(entity.getEmail(), entity);
            }
        } catch (IOException e) {
            throw new DAOException("Failed to load data from file: " + e.getMessage(), e);
        }
    }

    protected void saveToFile() throws DAOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(userStorage.values(), writer);
        } catch (IOException e) {
            throw new DAOException("Failed to save data to file: " + e.getMessage(), e);
        }
    }
}
