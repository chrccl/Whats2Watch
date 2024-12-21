package com.whats2watch.w2w.boundaries;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.controllers.RoomController;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Genre;
import com.whats2watch.w2w.model.Media;
import com.whats2watch.w2w.model.Room;
import com.whats2watch.w2w.model.User;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Set;

public class HomePageBoundary {

    private WhatsToWatch app;

    private User activeUser;

    private static final String DEFAULT_IMAGE_URL = "https://cdn.builder.io/api/v1/image/assets/TEMP/1fe73cf00781e4aa55b4f5a68876f1debb1b35519e409f807117d6bd4551511f?placeholderIfAbsent=true";

    @FXML
    private HBox roomGrid;

    @FXML
    private HBox genreGrid;

    @FXML
    private HBox trendingGrid;

    public void setMainApp(WhatsToWatch app, User user) {
        this.app = app;
        this.activeUser = user;
        populateGenreSection(RoomController.fetchGenres());
        try {
            populateTrending(RoomController.fetchTrendingMedias());
            populateRecentRooms(RoomController.fetchRecentRooms(activeUser));
        }catch(DAOException e) {
            roomGrid.getChildren().add(new Label("Something went wrong."));
        }catch(IOException | InterruptedException e){
            Thread.currentThread().interrupt();
            trendingGrid.getChildren().add(new Label("Something went wrong."));
        }
    }

    private void populateRecentRooms(Set<Room> rooms) {
        roomGrid.getChildren().clear();
        if(rooms.isEmpty()) {
            roomGrid.getChildren().add(new Label("No recent rooms found."));
        }else {
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

    private void populateGenreSection(Set<Genre> genres) {
        genreGrid.getChildren().clear();
        for (Genre genre : genres) {
            Label genreLabel = new Label(String.format("%s Room", genre.name()));
            genreLabel.getStyleClass().add("genre-name");

            ImageView plusButton = new ImageView(new Image("https://i.ibb.co/gRLsXCf/New-Room-Btn.png"));
            plusButton.getStyleClass().add("plus-button");
            plusButton.setFitWidth(24);  // Set button size
            plusButton.setFitHeight(24);
            plusButton.setOnMouseClicked(event -> newGenreRoomEvent(genre));

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

            Label trendingTitle = new Label(movie.getMediaId().getTitle());
            trendingTitle.getStyleClass().add("trending-title");

            trendingMovie.getChildren().addAll(trendingPoster, trendingTitle);

            trendingGrid.setSpacing(20);
            trendingGrid.getChildren().add(trendingMovie);
        }
    }

    private void newGenreRoomEvent(Genre genre){
        //TODO: send the user to the room page with the field to create a room set to the selected genre.
    }


}
