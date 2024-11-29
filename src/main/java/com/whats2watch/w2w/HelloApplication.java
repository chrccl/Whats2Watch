package com.whats2watch.w2w;

import com.whats2watch.w2w.model.Gender;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.TMDBFetcher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class HelloApplication extends Application {
    private static final String TITLE = "W2W - Whats2Watch";
    private static final int WIDTH = 1440;
    private static final int HEIGHT = 1024;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("registration-page.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), WIDTH, HEIGHT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("registration-page.css")).toExternalForm());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        TMDBFetcher tmdb = new TMDBFetcher();
        try {
            List<Movie> movies = tmdb.fetchTopMovies(2024);
            for (Movie movie : movies) {
                System.out.println(movie);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //launch();
    }
}