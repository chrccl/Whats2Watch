package com.whats2watch.w2w.model;

import com.whats2watch.w2w.WhatsToWatch;

import com.whats2watch.w2w.view.gui_graphic_controllers.homepage.HomePageBoundary;
import com.whats2watch.w2w.view.gui_graphic_controllers.matches.MatchesBoundary;
import com.whats2watch.w2w.view.gui_graphic_controllers.profile.ProfileBoundary;
import com.whats2watch.w2w.view.gui_graphic_controllers.register.RegisterBoundary;
import com.whats2watch.w2w.view.gui_graphic_controllers.room.RoomBoundary;
import com.whats2watch.w2w.view.gui_graphic_controllers.swipe.SwipeBoundary;
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
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("gui_view/login-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("gui_view/login-page.fxml"));
        AnchorPane root;
        try {
            root = fxmlLoader.load();
            RegisterBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(WhatsToWatch.class.getResource("gui_view/login-page.css")).toExternalForm());
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
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("gui_view/registration-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("gui_view/registration-page.fxml"));
        try {
            StackPane root = fxmlLoader.load();
            RegisterBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(WhatsToWatch.class.getResource("gui_view/registration-page.css")).toExternalForm());
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
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("gui_view/-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("gui_view/home-page.fxml"));
        try {
            StackPane root = fxmlLoader.load();
            HomePageBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this, user);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(WhatsToWatch.class.getResource("gui_view/home-page.css")).toExternalForm());
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
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("gui_view/room-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("gui_view/room-page.fxml"));
        try {
            VBox root = fxmlLoader.load();
            RoomBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this, user, genre);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(WhatsToWatch.class.getResource("gui_view/room-page.css")).toExternalForm());
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
    public void showProfilePage(User user) {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("gui_view/profile-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("gui_view/profile-page.fxml"));
        try {
            VBox root = fxmlLoader.load();
            ProfileBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this, user);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(WhatsToWatch.class.getResource("gui_view/profile-page.css")).toExternalForm());
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
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("gui_view/swipe-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("gui_view/swipe-page.fxml"));
        try {
            VBox root = fxmlLoader.load();
            SwipeBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this, user, room);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(WhatsToWatch.class.getResource("gui_view/swipe-page.css")).toExternalForm());
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
    public void showMatchesPage(User user, Room room) {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("gui_view/favouritesMediaSwipe-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("gui_view/favouritesMediaSwipe-page.fxml"));
        try {
            BorderPane root = fxmlLoader.load();
            MatchesBoundary controller = fxmlLoader.getController();
            controller.setMainApp(this, user, room);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(WhatsToWatch.class.getResource("gui_view/favouritesMediaSwipe-page.css")).toExternalForm());
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
