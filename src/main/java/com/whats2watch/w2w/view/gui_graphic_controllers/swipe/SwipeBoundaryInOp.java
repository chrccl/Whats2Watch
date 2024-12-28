package com.whats2watch.w2w.view.gui_graphic_controllers.swipe;

import com.whats2watch.w2w.exceptions.DAOException;

public interface SwipeBoundaryInOp {

    void passMediaEvent() throws DAOException;

    void likeMediaEvent() throws DAOException;

    void infoMediaEvent();

    void goToMatchesPageEvent() throws DAOException;
}
