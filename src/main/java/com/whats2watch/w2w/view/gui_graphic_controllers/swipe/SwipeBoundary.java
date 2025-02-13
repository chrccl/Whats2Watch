package com.whats2watch.w2w.view.gui_graphic_controllers.swipe;

import com.whats2watch.w2w.controllers.RoomController;
import com.whats2watch.w2w.controllers.SwipeController;
import com.whats2watch.w2w.exceptions.EntityCannotBePersistedException;
import com.whats2watch.w2w.exceptions.EntityNotFoundException;
import com.whats2watch.w2w.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class SwipeBoundary implements SwipeBoundaryInOp, SwipeBoundaryOutOp {

    private Dispatcher app;

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

    public void setMainApp(Dispatcher app, User user, Room room) {
        this.app = app;
        this.activeUser = user;
        this.room = room;
        this.roomMember = room.getRoomMembers().stream()
                .filter(rm -> rm.getUser().equals(user))
                .findFirst()
                .orElse(null);
        recommendMedias();
    }

    @Override
    public void recommendMedias() {
        try{
            mediaList = SwipeController.recommendMedias(room, roomMember);
            updateMediaCard();
        } catch (EntityNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Internal Error, please try again!");
            alert.showAndWait();
        }
    }

    @Override
    public void updateMediaCard() {
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
    @Override
    public void passMediaEvent() {
        roomMember.getPassedMedia().add(mediaList.get(currentIndex-1));
        updateMediaCard();
        if(currentIndex > 5) updateRecommendations();
    }

    @FXML
    @Override
    public void likeMediaEvent() {
        roomMember.getLikedMedia().add(mediaList.get(currentIndex-1));
        updateMediaCard();
        if(currentIndex > 5) updateRecommendations();
    }

    @FXML
    @Override
    public void infoMediaEvent() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Media media = mediaList.get(currentIndex-1);
        alert.setTitle(media.getMediaId().getTitle());
        alert.setContentText(media.toString());
        alert.showAndWait();
    }

    @Override
    public void updateRecommendations() {
        try{
            RoomController.updateRoomPreferences(room, roomMember);
            currentIndex = 0;
            recommendMedias();
        } catch (EntityCannotBePersistedException e) {
            currentIndex++;
        }
    }

    @FXML
    @Override
    public void goToMatchesPageEvent() {
        try {
            RoomController.updateRoomPreferences(room, roomMember);
            this.app.showMatchesPage(activeUser, room);
        } catch (EntityCannotBePersistedException e) {
            this.app.showMatchesPage(activeUser, room);
        }
    }
}
