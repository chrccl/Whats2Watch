package com.whats2watch.w2w.boundaries;

import com.whats2watch.w2w.controllers.RoomController;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.util.Set;

public class HomePageBoundary {

    private Dispatcher app;

    private User activeUser;

    private static final String DEFAULT_IMAGE_URL = "https://cdn.builder.io/api/v1/image/assets/TEMP/1fe73cf00781e4aa55b4f5a68876f1debb1b35519e409f807117d6bd4551511f?placeholderIfAbsent=true";

    @FXML
    private HBox roomGrid;

    @FXML
    private HBox genreGrid;

    @FXML
    private HBox trendingGrid;

    public void setMainApp(Dispatcher app, User user) {
        this.app = app;
        this.activeUser = user;
        populateGenreSection(RoomController.fetchGenres());
        try {
            populateTrending(RoomController.fetchTrendingMedias());
            populateRecentRooms(RoomController.fetchRecentRooms(activeUser));
        }catch(DAOException e) {
            Label noRoom = new Label("Something went wrong.");
            noRoom.setMaxWidth(150);
            noRoom.setMinWidth(150);
            noRoom.setTextAlignment(TextAlignment.CENTER);
            noRoom.setTextFill(Color.WHITE);
            roomGrid.getChildren().add(noRoom);
        }catch(IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            trendingGrid.getChildren().add(new Label("Something went wrong."));
        }
    }

    private void populateRecentRooms(Set<Room> rooms) {
        roomGrid.getChildren().clear();
        if(rooms.isEmpty()) {
            Label noRoom = new Label("No Recent Rooms found");
            noRoom.setMaxWidth(500);
            noRoom.setMinWidth(500);
            noRoom.setTextAlignment(TextAlignment.CENTER);
            noRoom.setTextFill(Color.WHITE);
            roomGrid.getChildren().add(noRoom);
        }else {
            for (Room room : rooms) {
                VBox roomCard = new VBox();
                roomCard.getStyleClass().add("room-card");

                ImageView roomImage = new ImageView(new Image(
                        room.getRoomMembers().stream()
                                .filter(member -> member.getUser().equals(activeUser)).findFirst()
                                .flatMap(member -> member.getLikedMedia().stream().findFirst()
                                        .map(media -> String.format("https://image.tmdb.org/t/p/w500%s", media.getPosterUrl())))
                                .orElse(DEFAULT_IMAGE_URL)
                ));
                roomImage.setFitHeight(120);
                roomImage.setFitWidth(100);

                roomImage.getStyleClass().add("room-image");

                Label roomName = new Label(room.getName());
                roomName.getStyleClass().add("room-name");

                HBox roomInfo = new HBox();
                roomInfo.getStyleClass().add("room-info");

                Label members = new Label(room.getRoomMembers().size() + " Members");
                members.getStyleClass().add("member-count");

                Label genres = room.getAllowedGenres() != null
                        ? new Label(room.getAllowedGenres().stream().findFirst().map(Object::toString).toString())
                        : new Label("No genres allowed");
                genres.getStyleClass().add("genre-label");

                roomInfo.getChildren().addAll(members, genres);
                roomCard.getChildren().addAll(roomImage, roomName, roomInfo);
                roomGrid.getChildren().add(roomCard);
            }
        }
    }

    private void populateGenreSection(Set<Genre> genres) {
        genreGrid.getChildren().clear();
        for (Genre genre : genres) {
            Label genreLabel = new Label(String.format("%s Room", genre.name()));
            genreLabel.getStyleClass().add("genre-name");

            ImageView plusButton = new ImageView(new Image("https://i.ibb.co/gRLsXCf/New-Room-Btn.png"));
            plusButton.getStyleClass().add("plus-button");
            plusButton.setFitWidth(24);  // Set button size
            plusButton.setFitHeight(24);
            plusButton.setOnMouseClicked(event -> {
                try {
                    newGenreRoomEvent(genre);
                } catch (IOException | DAOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            });

            HBox genreContent = new HBox(10);
            genreContent.setAlignment(Pos.CENTER_LEFT);
            genreContent.getChildren().addAll(genreLabel, plusButton);

            VBox genreCard = new VBox(5);
            genreCard.setAlignment(Pos.CENTER);
            genreCard.getStyleClass().add("genre-card");
            genreCard.getChildren().add(genreContent);

            genreGrid.setSpacing(20);
            genreGrid.getChildren().add(genreCard);
        }
    }

    private void populateTrending(Set<Media> movies) {
        trendingGrid.getChildren().clear();
        for (Media movie : movies) {
            VBox trendingMovie = new VBox();
            trendingMovie.getStyleClass().add("trending-movie");

            ImageView trendingPoster = new ImageView(new Image(String.format("https://image.tmdb.org/t/p/w500%s",movie.getPosterUrl())));
            trendingPoster.getStyleClass().add("trending-poster");
            trendingPoster.setFitWidth(150);
            trendingPoster.setPreserveRatio(true);

            Label trendingTitle = new Label(movie.getMediaId().getTitle());
            trendingTitle.setMaxWidth(300);
            trendingTitle.setMinWidth(300);
            trendingTitle.setWrapText(true);
            trendingTitle.setTextAlignment(TextAlignment.CENTER);
            trendingTitle.setTextFill(Color.WHITE);

            trendingMovie.getChildren().addAll(trendingPoster, trendingTitle);

            trendingGrid.setSpacing(20);
            trendingGrid.getChildren().add(trendingMovie);
        }
    }

    private void newGenreRoomEvent(Genre genre) throws IOException, DAOException {
        this.app.showRoomPage(activeUser, genre);
    }

    @FXML
    private void goToRoomPageEvent() throws IOException, DAOException {
        this.app.showRoomPage(activeUser,null);
    }

    @FXML
    private void goToUserPageEvent() throws IOException {
        this.app.showProfilePage(this.activeUser);
    }

}
