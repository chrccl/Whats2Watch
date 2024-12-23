package com.whats2watch.w2w.boundaries;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Room;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SwipeBoundary {

    private WhatsToWatch app;

    private User activeUser;

    private Room room;

    @FXML
    private Button likeButton;

    @FXML
    private Button dislikeButton;

    @FXML
    private ImageView mediaImage;

    @FXML
    private Label mediaTitle;

    @FXML
    private VBox mediaCard;

    public void setMainApp(WhatsToWatch app, User user, Room room) throws DAOException {
        this.app = app;
        this.activeUser = user;
        this.room = room;
        initializePage();
    }

    private void initializePage() throws DAOException {
        //TODO: load medias and ?sync room members?
    }

    private void updateMediaCard() {
        //TODO
    }

    @FXML
    private void passMediaEvent(){
        //TODO
    }

    @FXML
    private void likeMediaEvent(){
        //TODO
    }

    @FXML
    private void goToMatchesPageEvent() throws DAOException, IOException {
        this.app.showMatchesPage(activeUser, room);
    }
}
