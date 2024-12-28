package com.whats2watch.w2w.model;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.boundaries.*;
import com.whats2watch.w2w.exceptions.DAOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public class GUIDispatcher implements Dispatcher{

    private static final Logger logger = Logger.getLogger(GUIDispatcher.class.getName());

    private static final String TITLE = "W2W - Whats2Watch";

    private final Stage stage;

    public GUIDispatcher(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void showLoginPage(){
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("login-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("login-page.fxml"));
        AnchorPane root = null;
        try {
            root = fxmlLoader.load();
            RegisterBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("login-page.css")).toExternalForm());
            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void showRegisterPage() {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("registration-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("registration-page.fxml"));
        try {
            StackPane root = fxmlLoader.load();
            RegisterBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("registration-page.css")).toExternalForm());
            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void showHomePage(User user) {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("home-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("home-page.fxml"));
        try {
            StackPane root = fxmlLoader.load();
            HomePageBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this, user);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("home-page.css")).toExternalForm());
            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void showRoomPage(User user, Genre genre) {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("room-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("room-page.fxml"));
        try {
            VBox root = fxmlLoader.load();
            RoomBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this, user, genre);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("room-page.css")).toExternalForm());
            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException | DAOException e) {
            logger.severe(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void showProfilePage(User user) {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("profile-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("profile-page.fxml"));
        try {
            VBox root = fxmlLoader.load();
            ProfileBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this, user);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("profile-page.css")).toExternalForm());
            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void showSwipePage(User user, Room room){
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("swipe-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("swipe-page.fxml"));
        try {
            VBox root = fxmlLoader.load();
            SwipeBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this, user, room);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("swipe-page.css")).toExternalForm());
            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException | DAOException e) {
            logger.severe(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void showMatchesPage(User user, Room room) {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("favouritesMediaSwipe-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("favouritesMediaSwipe-page.fxml"));
        try {
            BorderPane root = fxmlLoader.load();
            MatchesBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this, user, room);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("favouritesMediaSwipe-page.css")).toExternalForm());
            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            System.exit(1);
        }
    }
}
