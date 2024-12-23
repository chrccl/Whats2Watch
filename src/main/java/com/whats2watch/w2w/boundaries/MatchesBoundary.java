package com.whats2watch.w2w.boundaries;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Media;
import com.whats2watch.w2w.model.Room;
import com.whats2watch.w2w.model.RoomMember;
import com.whats2watch.w2w.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MatchesBoundary {

    private WhatsToWatch app;

    private Room room;

    private RoomMember roomMember;

    @FXML
    private HBox movieGridMatches;

    @FXML
    private HBox movieGridLikes;

    public void setMainApp(WhatsToWatch app, User user, Room room) {
        this.app = app;
        this.room = room;
        this.roomMember = room.getRoomMembers().stream()
                .filter(rm -> rm.getUser().equals(user))
                .findFirst()
                .orElse(null);
        computeRoomMatches();
        showLikesGrid(roomMember.getLikedMedia());
    }

    private void computeRoomMatches() {
        List<Media> matches = room.getRoomMembers().stream()
                .map(RoomMember::getLikedMedia)
                .reduce((set1, set2) -> {
                    set1.retainAll(set2);
                    return set1;
                })
                .map(set -> set.stream()
                        .sorted(Comparator.comparing(Media::getPopularity).reversed())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
        updateMatchesGrid(matches);
    }

    private void updateMatchesGrid(List<Media> medias) {
        movieGridMatches.getChildren().clear(); // Clear existing movie cards

        for (Media movie : medias) {
            VBox movieCard = createMovieCard(movie);
            movieGridMatches.getChildren().add(movieCard);
        }
    }

    private void showLikesGrid(Set<Media> medias) {
        movieGridLikes.getChildren().clear();

        for (Media movie : medias) {
            VBox movieCard = createMovieCard(movie);
            movieGridLikes.getChildren().add(movieCard);
        }
    }

    private VBox createMovieCard(Media movie) {
        VBox movieCard = new VBox();
        movieCard.setAlignment(javafx.geometry.Pos.CENTER);
        movieCard.getStyleClass().add("movie-card");

        ImageView moviePoster = new ImageView(new Image(movie.getPosterUrl()));
        moviePoster.setFitWidth(100);
        moviePoster.setFitHeight(120);
        moviePoster.getStyleClass().add("movie-poster");

        Label movieTitle = new Label(movie.getMediaId().getTitle());
        movieTitle.getStyleClass().add("movie-title");

        movieCard.getChildren().addAll(moviePoster, movieTitle);
        return movieCard;
    }

    @FXML
    private void goToSwipePageEvent() throws DAOException, IOException {
        this.app.showSwipePage(roomMember.getUser(), room);
    }

}
