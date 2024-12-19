package com.whats2watch.w2w.boundaries;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.controllers.RoomController;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Room;
import com.whats2watch.w2w.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Set;

public class HomePageBoundary {

    private WhatsToWatch app;

    private User activeUser;

    private static final String DEFAULT_IMAGE_URL = "https://cdn.builder.io/api/v1/image/assets/TEMP/1fe73cf00781e4aa55b4f5a68876f1debb1b35519e409f807117d6bd4551511f?placeholderIfAbsent=true";

    @FXML
    private HBox roomGrid;

    public void setMainApp(WhatsToWatch app, User user) {
        this.app = app;
        this.activeUser = user;
        try {
            populateRecentRooms(RoomController.fetchRecentRooms(activeUser));
        }catch(DAOException e) {
            populateRecentRooms(null);
        }
    }

    private void populateRecentRooms(Set<Room> rooms) {
        roomGrid.getChildren().clear();
        for (Room room : rooms) {
            VBox roomCard = new VBox();
            roomCard.getStyleClass().add("room-card");

            ImageView roomImage = new ImageView(new Image(
                    room.getRoomMembers().stream()
                            .filter(member -> member.getUser().equals(activeUser)).findFirst()
                            .map(member -> member.getLikedMedia().iterator().next().getPosterUrl())
                            .orElse(DEFAULT_IMAGE_URL)
            ));
            roomImage.getStyleClass().add("room-image");

            Label roomName = new Label(room.getName());
            roomName.getStyleClass().add("room-name");

            HBox roomInfo = new HBox();
            roomInfo.getStyleClass().add("room-info");

            Label members = new Label(room.getRoomMembers().size() + " Members");
            members.getStyleClass().add("member-count");

            Label genres = new Label(room.getAllowedGenres().iterator().next().toString());
            genres.getStyleClass().add("genre-label");

            roomInfo.getChildren().addAll(members, genres);
            roomCard.getChildren().addAll(roomImage, roomName, roomInfo);
            roomGrid.getChildren().add(roomCard);
        }
    }

}
