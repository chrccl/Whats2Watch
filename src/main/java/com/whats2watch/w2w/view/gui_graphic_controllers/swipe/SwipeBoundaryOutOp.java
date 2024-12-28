package com.whats2watch.w2w.view.gui_graphic_controllers.swipe;

import com.whats2watch.w2w.exceptions.DAOException;

public interface SwipeBoundaryOutOp {

    void recommendMedias() throws DAOException;

    void updateMediaCard();

    void updateRecommendations() throws DAOException;
}
