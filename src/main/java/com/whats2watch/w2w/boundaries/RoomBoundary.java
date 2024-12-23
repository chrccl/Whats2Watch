package com.whats2watch.w2w.boundaries;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.controllers.RoomController;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dto.RoomValidator;
import com.whats2watch.w2w.model.dto.ValidationResult;
import com.whats2watch.w2w.model.dto.beans.RoomBean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoomBoundary {

    private WhatsToWatch app;

    private User activeUser;

    @FXML
    private TextField roomCodeField;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> genresField;

    @FXML
    private ComboBox<String> decadesField;

    @FXML
    private ComboBox<String> watchProvidersField;

    @FXML
    private ComboBox<String> productionCompaniesField;

    public void setMainApp(WhatsToWatch app, User user, Genre genre) throws DAOException {
        this.app = app;
        this.activeUser = user;
        initializePage(genre);
    }

    @FXML
    private void joinRoomEvent() throws DAOException, IOException {
        String roomCode = roomCodeField.getText();
        if (!roomCode.isEmpty() && !RoomController.addMemberToAnExistingRoom(activeUser, roomCode)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("No room related to this code.");
            alert.showAndWait();
        }
        this.app.showSwipePage(activeUser, roomCode);
    }

    @FXML
    private void createRoomEvent() throws DAOException, IOException {
        Integer decade = Integer.valueOf(decadesField.getSelectionModel().getSelectedItem().replace("s", ""));
        WatchProvider watchProvider = RoomController.getWatchProviderByName(watchProvidersField.getSelectionModel().getSelectedItem());
        ProductionCompany productionCompany = RoomController.getProductionCompanyByName(productionCompaniesField.getSelectionModel().getSelectedItem());
        Set<Genre> genres = genresField.getSelectionModel().getSelectedItem() != null
                ? Set.of(Genre.of(genresField.getSelectionModel().getSelectedItem()))
                : new HashSet<>(List.of(Genre.values()));
        Set<WatchProvider> watchProviders = watchProvider != null
                ? Set.of(watchProvider)
                : RoomController.fetchWatchProviders();
        Set<ProductionCompany> productionCompanies = productionCompany != null
                ? Set.of(productionCompany)
                : RoomController.fetchProductionCompanies();

        RoomBean roomBean = new RoomBean(nameField.getText(), MediaType.MOVIE, decade, genres, watchProviders, productionCompanies);
        ValidationResult validationResult = RoomValidator.validate(roomBean);
        if (validationResult.isValid()) {
            String roomCode = RoomController.saveRoom(activeUser, roomBean);
            this.app.showSwipePage(activeUser, roomCode);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText(validationResult.toString());
            alert.showAndWait();
        }
    }

    private void initializePage(Genre genre) throws DAOException {
        ObservableList<String> genres = FXCollections.observableArrayList(
                RoomController.fetchGenres().stream().map(Enum::name).collect(Collectors.toList())
        );
        genresField.setItems(genres);
        genresField.setEditable(false);
        if(genre != null) genresField.getSelectionModel().select(genre.name());

        ObservableList<String> decades = FXCollections.observableArrayList(
                IntStream.rangeClosed(1900, Year.now().getValue())
                        .filter(year -> year % 10 == 0)
                        .mapToObj(year -> year + "s").collect(Collectors.toList())
        );
        decadesField.setItems(decades);
        decadesField.setEditable(false);

        ObservableList<String> watchProviders = FXCollections.observableArrayList(
                RoomController.fetchWatchProviders().stream().map(WatchProvider::getProviderName).collect(Collectors.toList())
        );
        watchProvidersField.setItems(watchProviders);
        watchProvidersField.setEditable(false);

        ObservableList<String> productionCompanies = FXCollections.observableArrayList(
                RoomController.fetchProductionCompanies().stream().map(ProductionCompany::getCompanyName).collect(Collectors.toList())
        );
        productionCompaniesField.setItems(productionCompanies);
        productionCompaniesField.setEditable(false);
    }

}
