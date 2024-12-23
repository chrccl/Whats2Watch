package com.whats2watch.w2w.boundaries;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Room;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceType;

public class SwipeBoundary {

    private WhatsToWatch app;

    private User activeUser;

    private Room room;

    public void setMainApp(WhatsToWatch app, User user, String roomCode) throws DAOException {
        this.app = app;
        this.activeUser = user;
        this.room = (Room) PersistanceFactory.createDAO(PersistanceType.DEMO).createRoomDAO().findById(roomCode);
    }
}
