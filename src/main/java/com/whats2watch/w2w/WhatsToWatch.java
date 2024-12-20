package com.whats2watch.w2w;

import com.whats2watch.w2w.boundaries.HomePageBoundary;
import com.whats2watch.w2w.boundaries.ProfileBoundary;
import com.whats2watch.w2w.boundaries.RegisterBoundary;
import com.whats2watch.w2w.boundaries.RoomBoundary;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.Genre;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import java.util.Objects;

public class WhatsToWatch extends Application {
    private static final String TITLE = "W2W - Whats2Watch";

    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        this.showLoginPage();
    }

    public void showLoginPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("login-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("login-page.fxml"));
        AnchorPane root = fxmlLoader.load();
        RegisterBoundary controller = fxmlLoader.getController();
        controller.setMainApp(this);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("login-page.css")).toExternalForm());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void showRegisterPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("registration-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("registration-page.fxml"));
        StackPane root = fxmlLoader.load();
        RegisterBoundary controller = fxmlLoader.getController();
        controller.setMainApp(this);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("registration-page.css")).toExternalForm());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void showHomePage(User user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("home-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("home-page.fxml"));
        StackPane root = fxmlLoader.load();
        HomePageBoundary controller = fxmlLoader.getController();
        controller.setMainApp(this, user);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("home-page.css")).toExternalForm());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void showRoomPage(Genre genre) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("room-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("room-page.fxml"));
        VBox root = fxmlLoader.load();
        RoomBoundary controller = fxmlLoader.getController();
        controller.setMainApp(this, genre);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("room-page.css")).toExternalForm());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void showProfilePage(User user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("profile-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("profile-page.fxml"));
        VBox root = fxmlLoader.load();
        ProfileBoundary controller = fxmlLoader.getController();
        controller.setMainApp(this, user);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("profile-page.css")).toExternalForm());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}