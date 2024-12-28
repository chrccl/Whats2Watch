package com.whats2watch.w2w.cli_view;

import com.whats2watch.w2w.controllers.RoomController;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;

public class HomePageView {

    private static Dispatcher app;
    private static User activeUser;

    private static Set<Room> recentRooms;
    private static Set<Media> trendingMedias;
    private static Set<Genre> genres;

    private static final String ERROR = "Error";

    public static void setMainApp(Dispatcher d, User user) {
        if(app != null) app = d;
        if(activeUser != null) activeUser = user;

        // Precompute data
        if(genres != null) genres = RoomController.fetchGenres();
        try {
            if (trendingMedias != null) trendingMedias = RoomController.fetchTrendingMedias();
            if (recentRooms != null) recentRooms = RoomController.fetchRecentRooms(activeUser);
        } catch (DAOException | InterruptedException | IOException e) {
            System.err.println(ERROR + ": " + e.getMessage());
            recentRooms = Collections.emptySet();
            trendingMedias = Collections.emptySet();
        }

        showMenu();
    }

    private static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        String action;
        do {
            System.out.println("--- Home Page ---");
            System.out.println("Type 'T' to view Trending, 'R' to view Recent Rooms, or 'G' to view Genres");
            System.out.println("Type 'ROOM' to go to Room Page or 'EXIT' to quit");
            action = scanner.nextLine().toUpperCase();
        } while (!action.equals("T") && !action.equals("R") && !action.equals("G") && !action.equals("ROOM") && !action.equals("EXIT"));

        try {
            switch (action) {
                case "T":
                    displayTrending();
                    break;
                case "R":
                    displayRecentRooms();
                    break;
                case "G":
                    displayGenres();
                    break;
                case "ROOM":
                    goToRoomPageEvent();
                    break;
                case "EXIT":
                    System.out.println("Exiting Home Page...");
                    return;
            }
        } catch (Exception e) {
            System.err.println(ERROR + ": " + e.getMessage());
        }

        // Loop back to menu
        showMenu();
    }

    private static void displayRecentRooms() {
        System.out.println("--- Recent Rooms ---");
        if (recentRooms.isEmpty()) {
            System.out.println("No Recent Rooms found.");
        } else {
            for (Room room : recentRooms) {
                System.out.println("Room: " + room.getName());
            }
        }
    }

    private static void displayTrending() {
        System.out.println("--- Trending Medias ---");
        if (trendingMedias.isEmpty()) {
            System.out.println("No trending media available.");
        } else {
            for (Media media : trendingMedias) {
                System.out.println("Media: " + media.getMediaId().getTitle());
            }
        }
    }

    private static void displayGenres() {
        System.out.println("--- Genres ---");
        if (genres.isEmpty()) {
            System.out.println("No genres available.");
        } else {
            for (Genre genre : genres) {
                System.out.println("Genre: " + genre.name());
            }
        }
    }

    private static void goToRoomPageEvent() {
        System.out.println("Navigating to Room Page...");
        app.showRoomPage(activeUser, null);
    }
}
