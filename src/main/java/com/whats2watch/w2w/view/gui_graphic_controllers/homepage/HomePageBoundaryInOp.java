package com.whats2watch.w2w.view.gui_graphic_controllers.homepage;

import com.whats2watch.w2w.model.Genre;

public interface HomePageBoundaryInOp {

    void newGenreRoomEvent(Genre genre);

    void goToRoomPageEvent();

    void goToUserPageEvent();

}
