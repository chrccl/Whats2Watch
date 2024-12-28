package com.whats2watch.w2w.view.gui_graphic_controllers.homepage;

import com.whats2watch.w2w.model.Genre;
import com.whats2watch.w2w.model.Media;
import com.whats2watch.w2w.model.Room;

import java.util.Set;

public interface HomePageBoundaryOutOp {

    void populateRecentRooms(Set<Room> rooms);

    void populateGenreSection(Set<Genre> genres);

    void populateTrending(Set<Media> movies);

}
