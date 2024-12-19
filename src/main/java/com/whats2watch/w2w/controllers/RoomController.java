package com.whats2watch.w2w.controllers;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Room;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceType;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RoomController {

    private RoomController() {
        throw new UnsupportedOperationException("RoomController is a utility class and cannot be instantiated.");
    }

    public static Set<Room> fetchRecentRooms(User user) throws DAOException {
        return PersistanceFactory
                .createDAO(PersistanceType.DEMO)
                .createRoomDAO()
                .findAll()
                .stream()
                .map(room -> (Room) room)
                .filter(room -> room.getRoomMembers()
                        .stream()
                        .anyMatch(member -> member.getUser().equals(user)))
                .sorted(Comparator.comparing(Room::getCreationDate).reversed()) //from the newest to the oldest
                .collect(Collectors.toCollection(LinkedHashSet::new)); //to preserve order
    }

}
