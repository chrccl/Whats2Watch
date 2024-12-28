package com.whats2watch.w2w.view.cli_view;

import com.whats2watch.w2w.controllers.RoomController;
import com.whats2watch.w2w.controllers.SwipeController;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SwipeView {

    private static Dispatcher app;

    private static User activeUser;

    private static Room room;

    private static RoomMember roomMember;

    private static List<Media> mediaList;

    private static Integer currentIndex = 0;

    private static final String DEFAULT_IMAGE_URL = "https://cdn.builder.io/api/v1/image/assets/TEMP/1fe73cf00781e4aa55b4f5a68876f1debb1b35519e409f807117d6bd4551511f?placeholderIfAbsent=true";

    private static final String DELIMITER = "====================================";

    private SwipeView() {
        throw new UnsupportedOperationException("SwipeView is a utility class and cannot be instantiated.");
    }

    public static void initPage(Dispatcher d, User user, Room r) {
        app = d;
        activeUser = user;
        room = r;
        roomMember = room.getRoomMembers().stream()
                .filter(rm -> rm.getUser().equals(user))
                .findFirst()
                .orElse(null);
        try {
            recommendMedias();
        } catch (DAOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        showMenu();
    }

    private static void recommendMedias() throws DAOException {
        mediaList = SwipeController.recommendMedias(room, roomMember);
        currentIndex = 0;
    }

    private static void showMediaCard() {
        if (currentIndex < mediaList.size()) {
            Media currentMedia = mediaList.get(currentIndex);
            String posterUrl = currentMedia.getPosterUrl();
            String imageUrl = (posterUrl != null && !posterUrl.isEmpty())
                    ? String.format("https://image.tmdb.org/t/p/w500%s", posterUrl)
                    : DEFAULT_IMAGE_URL;

            System.out.println(DELIMITER);
            System.out.println("Now Showing: " + currentMedia.getMediaId().getTitle());
            System.out.println("Poster URL: " + imageUrl);
            System.out.println("Description: " + currentMedia.getPlot());
            System.out.println(DELIMITER);
        } else {
            System.out.println("No more media to display.");
        }
    }

    private static void passMediaEvent() throws DAOException {
        if (currentIndex < mediaList.size()) {
            roomMember.getPassedMedia().add(mediaList.get(currentIndex));
            currentIndex++;
            checkRecommendations();
        } else {
            System.out.println("No media left to pass.");
        }
    }

    private static void likeMediaEvent() throws DAOException {
        if (currentIndex < mediaList.size()) {
            roomMember.getLikedMedia().add(mediaList.get(currentIndex));
            currentIndex++;
            checkRecommendations();
        } else {
            System.out.println("No media left to like.");
        }
    }

    private static void infoMediaEvent() {
        if (currentIndex < mediaList.size()) {
            Media currentMedia = mediaList.get(currentIndex);
            System.out.println(DELIMITER);
            System.out.println("Information about: " + currentMedia.getMediaId().getTitle());
            System.out.println(currentMedia);
            System.out.println(DELIMITER);
        } else {
            System.out.println("No media information available.");
        }
    }

    private static void checkRecommendations() throws DAOException {
        if (currentIndex >= 5) {
            RoomController.updateRoomPreferences(room, roomMember);
            recommendMedias();
        }
    }

    private static void printLikedMediaEvent() {
        System.out.println("=== Your Liked Medias ===");
        printMediaList(new ArrayList<>(roomMember.getLikedMedia()));
    }

    private static void showRoomMatches() throws DAOException {
        RoomController.updateRoomPreferences(room, roomMember);
        System.out.println("=== Room Matches ===");
        printMediaList(RoomController.getRoomMatches(room));
    }

    private static void printMediaList(List<Media> medias){
        medias.forEach(media -> {
            System.out.println(media.getMediaId().getTitle() + " - " + media.getMediaId().getYear());
            System.out.println(media.getPlot());
            System.out.println("Trailer: " + media.getVideoUrl());
            System.out.println("Actors: " + media.getCharacters().toString());
            System.out.println("Watch Providers: " + media.getWatchProviders().toString());
            System.out.println("Production Companies: " + media.getProductionCompanies().stream().limit(5).collect(Collectors.toSet()));
            System.out.println("===================================");
        });
    }

    private static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            showMediaCard();
            System.out.println("Options:");
            System.out.println("1. Pass Media");
            System.out.println("2. Like Media");
            System.out.println("3. More Info");
            System.out.println("4. Show Your Likes");
            System.out.println("5. Show Room Matches");
            System.out.println("0. Back to Homepage");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            try {
                switch (choice) {
                    case 1:
                        passMediaEvent();
                        break;
                    case 2:
                        likeMediaEvent();
                        break;
                    case 3:
                        infoMediaEvent();
                        break;
                    case 4:
                        printLikedMediaEvent();
                        break;
                    case 5:
                        showRoomMatches();
                        break;
                    case 0:
                        System.out.println("Returning to Homepage...");
                        app.showHomePage(activeUser);
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (DAOException e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }
}
