package com.whats2watch.w2w.view.gui_graphic_controllers.matches;

import com.whats2watch.w2w.controllers.RoomController;
import com.whats2watch.w2w.model.Media;
import com.whats2watch.w2w.model.Room;
import com.whats2watch.w2w.model.RoomMember;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.Dispatcher;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Set;

public class MatchesBoundary implements MatchesBoundaryInOp, MatchesBoundaryOutOp {

    private Dispatcher app;

    private Room room;

    private RoomMember roomMember;

    @FXML
    private HBox movieGridMatches;

    @FXML
    private HBox movieGridLikes;

    public void setMainApp(Dispatcher app, User user, Room room) {
        this.app = app;
        this.room = room;
        this.roomMember = room.getRoomMembers().stream()
                .filter(rm -> rm.getUser().equals(user))
                .findFirst()
                .orElse(null);
        computeRoomMatches();
        showLikesGrid(roomMember.getLikedMedia());
    }

    @Override
    public void computeRoomMatches() {
        updateMatchesGrid(RoomController.getRoomMatches(room));
    }

    @Override
    public void updateMatchesGrid(List<Media> medias) {
        movieGridMatches.getChildren().clear(); // Clear existing movie cards

        for (Media movie : medias) {
            VBox movieCard = createMovieCard(movie);
            movieGridMatches.getChildren().add(movieCard);
        }
    }

    @Override
    public void showLikesGrid(Set<Media> medias) {
        movieGridLikes.getChildren().clear();

        for (Media movie : medias) {
            VBox movieCard = createMovieCard(movie);
            movieGridLikes.getChildren().add(movieCard);
        }
    }

    @Override
    public VBox createMovieCard(Media media) {
        VBox movieCard = new VBox();
        movieCard.setAlignment(javafx.geometry.Pos.CENTER);
        movieCard.getStyleClass().add("movie-card");

        ImageView moviePoster = new ImageView(new Image(String.format("https://image.tmdb.org/t/p/w500%s", media.getPosterUrl())));
        moviePoster.setFitWidth(100);
        moviePoster.setFitHeight(120);
        moviePoster.getStyleClass().add("movie-poster");

        Label movieTitle = new Label(media.getMediaId().getTitle());
        movieTitle.getStyleClass().add("movie-title");

        movieCard.getChildren().addAll(moviePoster, movieTitle);
        return movieCard;
    }

    @FXML
    @Override
    public void goToSwipePageEvent() {
        this.app.showSwipePage(roomMember.getUser(), room);
    }

}
