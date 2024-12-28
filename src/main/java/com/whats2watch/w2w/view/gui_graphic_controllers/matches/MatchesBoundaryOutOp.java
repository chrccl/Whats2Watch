package com.whats2watch.w2w.view.gui_graphic_controllers.matches;

import com.whats2watch.w2w.model.Media;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Set;

public interface MatchesBoundaryOutOp {

    void computeRoomMatches();

    void updateMatchesGrid(List<Media> medias);

    void showLikesGrid(Set<Media> medias);

    VBox createMovieCard(Media media);

}
