package com.whats2watch.w2w.boundaries;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.controllers.RoomController;
import com.whats2watch.w2w.controllers.SwipeController;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Media;
import com.whats2watch.w2w.model.Room;
import com.whats2watch.w2w.model.RoomMember;
import com.whats2watch.w2w.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.List;

public class SwipeBoundary {

    private WhatsToWatch app;

    private User activeUser;

    private Room room;

    private RoomMember roomMember;

    private List<Media> mediaList;

    private Integer currentIndex = 0; // Tracks the current media index

    @FXML
    private ImageView mediaImage;

    @FXML
    private Label mediaTitle;

    private static final String DEFAULT_IMAGE_URL = "https://cdn.builder.io/api/v1/image/assets/TEMP/1fe73cf00781e4aa55b4f5a68876f1debb1b35519e409f807117d6bd4551511f?placeholderIfAbsent=true";

    public void setMainApp(WhatsToWatch app, User user, Room room) throws DAOException {
        this.app = app;
        this.activeUser = user;
        this.room = room;
        this.roomMember = room.getRoomMembers().stream()
                .filter(rm -> rm.getUser().equals(user))
                .findFirst()
                .orElse(null);
        recommendMedias();
    }

    private void recommendMedias() throws DAOException {
        mediaList = SwipeController.recommendMedias(room, roomMember);
        updateMediaCard();
    }

    private void updateMediaCard() {
        String posterUrl = mediaList.get(currentIndex).getPosterUrl();
        mediaImage.setImage(new Image(posterUrl != null && !posterUrl.isEmpty()
                ? String.format("https://image.tmdb.org/t/p/w500%s", mediaList.get(currentIndex).getPosterUrl())
                : DEFAULT_IMAGE_URL));
        mediaImage.setFitHeight(500);
        mediaImage.setFitWidth(300);
        mediaTitle.setText(mediaList.get(currentIndex).getMediaId().getTitle());
        currentIndex++;
    }

    @FXML
    private void passMediaEvent() throws DAOException {
        roomMember.getPassedMedia().add(mediaList.get(currentIndex-1));
        updateMediaCard();
        if(currentIndex > 15) updateRecommendations();
    }

    @FXML
    private void likeMediaEvent() throws DAOException {
        roomMember.getLikedMedia().add(mediaList.get(currentIndex-1));
        updateMediaCard();
        if(currentIndex > 15) updateRecommendations();
    }

    private void updateRecommendations() throws DAOException {
        RoomController.updateRoomPreferences(room, roomMember);
        currentIndex = 0;
        recommendMedias();
    }

    @FXML
    private void goToMatchesPageEvent() throws IOException {
        this.app.showMatchesPage(activeUser, room);
    }
}
