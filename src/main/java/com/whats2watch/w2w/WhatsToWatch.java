package com.whats2watch.w2w;

import com.whats2watch.w2w.controllers.RegisterController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import java.util.Objects;

public class WhatsToWatch extends Application {
    private static final String TITLE = "W2W - Whats2Watch";
    private static final int WIDTH = 1440;
    private static final int HEIGHT = 1024;

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
        RegisterController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("login-page.css")).toExternalForm());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void showRegisterPage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("registration-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("registration-page.fxml"));
        StackPane root = fxmlLoader.load();
        RegisterController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("registration-page.css")).toExternalForm());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}