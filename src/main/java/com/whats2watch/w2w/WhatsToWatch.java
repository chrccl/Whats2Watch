package com.whats2watch.w2w;

import com.whats2watch.w2w.boundaries.*;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.dao_factories.PersistenceType;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Logger;

public class WhatsToWatch extends Application {

    private static final String TITLE = "W2W - Whats2Watch";

    private static PersistenceType persistenceType;

    private static Logger logger = Logger.getLogger(WhatsToWatch.class.getName());

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
        stage.setMaximized(true);
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
        stage.setMaximized(true);
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
        stage.setMaximized(true);
        stage.show();
    }

    public void showRoomPage(User user, Genre genre) throws IOException, DAOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("room-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("room-page.fxml"));
        VBox root = fxmlLoader.load();
        RoomBoundary controller = fxmlLoader.getController();
        controller.setMainApp(this, user, genre);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("room-page.css")).toExternalForm());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setMaximized(true);
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
        stage.setMaximized(true);
        stage.show();
    }

    public void showSwipePage(User user, Room room) throws IOException, DAOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("swipe-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("swipe-page.fxml"));
        VBox root = fxmlLoader.load();
        SwipeBoundary controller = fxmlLoader.getController();
        controller.setMainApp(this, user, room);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("swipe-page.css")).toExternalForm());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void showMatchesPage(User user, Room room) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WhatsToWatch.class.getResource("favouritesMediaSwipe-page.fxml"));
        fxmlLoader.setLocation(WhatsToWatch.class.getResource("favouritesMediaSwipe-page.fxml"));
        BorderPane root = fxmlLoader.load();
        MatchesBoundary controller = fxmlLoader.getController();
        controller.setMainApp(this, user, room);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("favouritesMediaSwipe-page.css")).toExternalForm());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private static PersistenceType choosePersistenceType() {
        Scanner scanner = new Scanner(System.in);
        String pt;
        do{
            logger.info("Seleziona la modalità di Persistenza (DB - FS - DEMO):");
            pt = scanner.nextLine();
        }while(!pt.equals("DB") && !pt.equals("FS") && !pt.equals("DEMO"));
        if(pt.equals("DB")){
            return PersistenceType.DATABASE;
        }else if(pt.equals("FS")){
            return PersistenceType.FILESYSTEM;
        }else {
            return PersistenceType.DEMO;
        }
    }

    public static PersistenceType getPersistanceType(){
        return persistenceType;
    }

    public static void main(String[] args) {
        logger.info("BENVENUTO IN WHATS2WATCH!!!");
        persistenceType = choosePersistenceType();
        launch();
    }
}