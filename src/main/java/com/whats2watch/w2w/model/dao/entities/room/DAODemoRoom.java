package com.whats2watch.w2w.model.dao.entities.room;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Room;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.user.DAODemoUser;

import java.util.HashSet;
import java.util.Set;

public class DAODemoRoom implements DAO<Room, String> {

    private static Set<Room> rooms;
    private static DAODemoRoom instance;

    private DAODemoRoom() {
        rooms = new HashSet<>();
    }

    public static synchronized DAODemoRoom getInstance() {
        if (instance == null) {
            instance = new DAODemoRoom();
        }
        return instance;
    }

    @Override
    public void save(Room entity) throws DAOException {
        rooms.add(entity);
    }

    @Override
    public Room findById(String entityKey) throws DAOException {
        return rooms.stream()
                .filter(room -> room.getCode().equals(entityKey))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        Room room = findById(entityKey);
        if (room != null)
            rooms.remove(findById(entityKey));
    }

    @Override
    public Set<Room> findAll() throws DAOException  {
        return rooms;
    }
}
