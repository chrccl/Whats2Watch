package com.whats2watch.w2w.view.gui_graphic_controllers.room;

import com.whats2watch.w2w.exceptions.DAOException;

public interface RoomBoundaryInOp {

    void joinRoomEvent() throws DAOException;

    void createRoomEvent() throws DAOException;

    void goToHomePageEvent();

    void goToUserPageEvent();

}
