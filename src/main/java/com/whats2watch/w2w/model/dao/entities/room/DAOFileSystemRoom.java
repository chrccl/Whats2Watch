package com.whats2watch.w2w.model.dao.entities.room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Room;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.io.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DAOFileSystemRoom implements DAO<Room, String> {

    private final String filePath;
    private final Map<String, Room> roomStorage;
    private final Gson gson;

    public DAOFileSystemRoom(String filePath) throws DAOException {
        this.filePath = filePath;
        this.roomStorage = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadFromFile();
    }

    @Override
    public void save(Room entity) throws DAOException {
        roomStorage.put(entity.getCode(), entity);
        saveToFile();
    }

    @Override
    public Room findById(String entityKey) throws DAOException {
        Room entity = roomStorage.get(entityKey);
        if (entity == null) {
            throw new DAOException("Room not found with key: " + entityKey);
        }
        return entity;
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        if (roomStorage.remove(entityKey) == null) {
            throw new DAOException("Room not found with key: " + entityKey);
        }
        saveToFile();
    }

    @Override
    public Set<Room> findAll() throws DAOException {
        return new HashSet<>(roomStorage.values());
    }

    protected void loadFromFile() throws DAOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (Reader reader = new FileReader(file)) {
            Room[] entities = gson.fromJson(reader, Room[].class);
            for (Room entity : entities) {
                roomStorage.put(entity.getCode(), entity);
            }
        } catch (IOException e) {
            throw new DAOException("Failed to load data from file: " + e.getMessage(), e);
        }
    }

    protected void saveToFile() throws DAOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(roomStorage.values(), writer);
        } catch (IOException e) {
            throw new DAOException("Failed to save data to file: " + e.getMessage(), e);
        }
    }
}
