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
        mediaImage.setImage(new Image(mediaList.get(currentIndex).getPosterUrl()));
        mediaTitle.setText(mediaList.get(currentIndex).getMediaId().getTitle());
        currentIndex++;
    }

    @FXML
    private void passMediaEvent() throws DAOException {
        if(currentIndex > 5) {
            updateRecommendations();
        }
        roomMember.getPassedMedia().add(mediaList.get(currentIndex-1));
    }

    @FXML
    private void likeMediaEvent() throws DAOException {
        if(currentIndex > 5) {
            updateRecommendations();
        }
        roomMember.getLikedMedia().add(mediaList.get(currentIndex-1));
    }

    private void updateRecommendations() throws DAOException {
        // Add updated room member
        room.getRoomMembers().remove(roomMember);
        room.getRoomMembers().add(roomMember);
        RoomController.updateRoom(room);
        currentIndex = 0;
        recommendMedias();
    }

    @FXML
    private void goToMatchesPageEvent() throws DAOException, IOException {
        this.app.showMatchesPage(activeUser, room);
    }
}
