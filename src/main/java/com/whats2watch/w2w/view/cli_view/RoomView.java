package com.whats2watch.w2w.view.cli_view;

import com.whats2watch.w2w.controllers.RoomController;
import com.whats2watch.w2w.exceptions.EntityCannotBePersistedException;
import com.whats2watch.w2w.exceptions.EntityNotFoundException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dto.RoomValidator;
import com.whats2watch.w2w.model.dto.ValidationResult;
import com.whats2watch.w2w.model.dto.beans.RoomBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class RoomView {

    private static Dispatcher app;

    private static User activeUser;

    private static Set<Genre> genresCache;

    private static Set<WatchProvider> watchProvidersCache;

    private static Set<ProductionCompany> productionCompaniesCache;

    private static List<String> decadesCache;

    private static final String LIST_FORMAT = "%d. %s%n";

    private RoomView() {
        throw new UnsupportedOperationException("RoomView is a utility class and cannot be instantiated.");
    }

    public static void showMenu(Dispatcher d, User user, Genre initialGenre) {
        if (app == null) app = d;
        if(activeUser == null) activeUser = user;

        initializePage();

        Scanner scanner = new Scanner(System.in);
        String action;
        do {
            System.out.println("Select an option:");
            System.out.println("1. Join a Room");
            System.out.println("2. Create a Room");
            System.out.println("3. Go to Home Page");
            System.out.println("4. Go to User Page");
            System.out.println("5. Exit");
            action = scanner.nextLine();

            switch (action) {
                case "1":
                    handleJoinRoom(scanner);
                    break;
                case "2":
                    handleCreateRoom(scanner, initialGenre);
                    break;
                case "3":
                    app.showHomePage(activeUser);
                    break;
                case "4":
                    app.showProfilePage(activeUser);
                    break;
                case "5":
                    System.out.println("Exiting Room Menu.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (!"5".equals(action));
    }

    private static void handleJoinRoom(Scanner scanner) {
        System.out.print("Enter the Room Code: ");
        String roomCode = scanner.nextLine();
        try{
            Room room = RoomController.addMemberToAnExistingRoom(activeUser, roomCode);
            app.showSwipePage(activeUser, room);
        } catch (EntityNotFoundException e) {
            System.out.println("No room found with the provided code.");
        }
    }

    private static void handleCreateRoom(Scanner scanner, Genre selectedGenre) {
        System.out.print("Enter Room Name: ");
        String name = scanner.nextLine();

        Genre genre = selectedGenre != null ? selectedGenre : selectGenre(scanner);
        Integer decade = selectDecade(scanner);
        WatchProvider watchProvider = selectWatchProvider(scanner);
        ProductionCompany productionCompany = selectProductionCompany(scanner);

        Set<Genre> genres = genre != null ? Set.of(genre) : null;
        Set<WatchProvider> watchProviders = watchProvider != null ? Set.of(watchProvider) : null;
        Set<ProductionCompany> productionCompanies = productionCompany != null ? Set.of(productionCompany) : null;

        RoomBean roomBean = new RoomBean(name, MediaType.MOVIE, decade, genres, watchProviders, productionCompanies);
        ValidationResult validationResult = RoomValidator.validate(roomBean);

        if (validationResult.isValid()) {
            try {
                Room room = RoomController.saveRoom(activeUser, roomBean);
                app.showSwipePage(activeUser, room);
            } catch (EntityCannotBePersistedException e) {
                System.out.println("Internal error: please try again");
            }
        } else {
            System.out.println("Validation Error: " + validationResult);
        }
    }

    private static Genre selectGenre(Scanner scanner) {
        System.out.println("Available Genres:");
        List<Genre> genresList = new ArrayList<>(genresCache);
        for (int i = 0; i < genresList.size(); i++) {
            System.out.printf(LIST_FORMAT, i + 1, genresList.get(i).name());
        }
        System.out.print("Select a genre (or press Enter to skip): ");
        String input = scanner.nextLine();
        if (input.isEmpty()) return null;
        try {
            int choice = Integer.parseInt(input);
            return genresList.get(choice - 1);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Invalid choice. No genre selected.");
            return null;
        }
    }

    private static Integer selectDecade(Scanner scanner) {
        System.out.println("Available Decades:");
        for (int i = 0; i < decadesCache.size(); i++) {
            System.out.printf(LIST_FORMAT, i + 1, decadesCache.get(i));
        }
        System.out.print("Select a decade (or press Enter to skip): ");
        String input = scanner.nextLine();
        if (input.isEmpty()) return null;
        try {
            int choice = Integer.parseInt(input);
            return Integer.valueOf(decadesCache.get(choice - 1).replace("s", ""));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Invalid choice. No decade selected.");
            return null;
        }
    }

    private static WatchProvider selectWatchProvider(Scanner scanner) {
        System.out.println("Available Watch Providers:");
        List<WatchProvider> watchProvidersList = new ArrayList<>(watchProvidersCache);
        for (int i = 0; i < watchProvidersList.size(); i++) {
            System.out.printf(LIST_FORMAT, i + 1, watchProvidersList.get(i).getProviderName());
        }
        System.out.print("Select a watch provider (or press Enter to skip): ");
        String input = scanner.nextLine();
        if (input.isEmpty()) return null;
        try {
            int choice = Integer.parseInt(input);
            return watchProvidersList.get(choice - 1);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Invalid choice. No watch provider selected.");
            return null;
        }
    }

    private static ProductionCompany selectProductionCompany(Scanner scanner) {
        System.out.println("Available Production Companies:");
        List<ProductionCompany> productionCompaniesList = new ArrayList<>(productionCompaniesCache);
        for (int i = 0; i < productionCompaniesList.size(); i++) {
            System.out.printf(LIST_FORMAT, i + 1, productionCompaniesList.get(i).getCompanyName());
        }
        System.out.print("Select a production company (or press Enter to skip): ");
        String input = scanner.nextLine();
        if (input.isEmpty()) return null;
        try {
            int choice = Integer.parseInt(input);
            return productionCompaniesList.get(choice - 1);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Invalid choice. No production company selected.");
            return null;
        }
    }

    private static void initializePage() {
        try {
            if (genresCache == null) genresCache = RoomController.fetchGenres();
            if (watchProvidersCache == null) watchProvidersCache = RoomController.fetchWatchProviders();
            if (productionCompaniesCache == null) productionCompaniesCache = RoomController.fetchProductionCompanies();
            if (decadesCache == null) {
                decadesCache = new ArrayList<>();
                for (int year = 1900; year <= LocalDate.now().getYear(); year += 10) {
                    decadesCache.add(year + "s");
                }
            }
        } catch (EntityNotFoundException e) {
            System.out.println("Error initializing page: " + e.getMessage());
        }
    }
}
