package com.whats2watch.w2w.view.gui_graphic_controllers.room;

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

import java.time.Year;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RoomBoundary implements RoomBoundaryInOp, RoomBoundaryOutOp{

    private Dispatcher app;

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

    public void setMainApp(Dispatcher app, User user, Genre genre) throws DAOException {
        this.app = app;
        this.activeUser = user;
        initializePage(genre);
    }

    @FXML
    @Override
    public void joinRoomEvent() throws DAOException {
        String roomCode = roomCodeField.getText();
        Room room = RoomController.addMemberToAnExistingRoom(activeUser, roomCode);
        if (!roomCode.isEmpty() && room != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("No room related to this code.");
            alert.showAndWait();
        }else{
            this.app.showSwipePage(activeUser, room);
        }
    }

    @FXML
    @Override
    public void createRoomEvent() throws DAOException {
        WatchProvider watchProvider = RoomController.getWatchProviderByName(
                watchProvidersField.getSelectionModel().getSelectedItem());
        ProductionCompany productionCompany = RoomController.getProductionCompanyByName(
                productionCompaniesField.getSelectionModel().getSelectedItem());
        Integer decade = decadesField.getSelectionModel().getSelectedItem() != null
                ? Integer.valueOf(decadesField.getSelectionModel().getSelectedItem().replace("s", ""))
                : null;
        Set<Genre> genres = genresField.getSelectionModel().getSelectedItem() != null
                ? Set.of(Genre.of(genresField.getSelectionModel().getSelectedItem()))
                : null;
        Set<WatchProvider> watchProviders = watchProvider != null
                ? Set.of(watchProvider)
                : null;
        Set<ProductionCompany> productionCompanies = productionCompany != null
                ? Set.of(productionCompany)
                : null;

        RoomBean roomBean = new RoomBean(nameField.getText(), MediaType.MOVIE, decade, genres, watchProviders, productionCompanies);
        ValidationResult validationResult = RoomValidator.validate(roomBean);
        if (validationResult.isValid()) {
            Room room = RoomController.saveRoom(activeUser, roomBean);
            this.app.showSwipePage(activeUser, room);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText(validationResult.toString());
            alert.showAndWait();
        }
    }

    @Override
    public void initializePage(Genre genre) throws DAOException {
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

        Set<WatchProvider> watchProvidersCache = RoomController.fetchWatchProviders();
        ObservableList<String> watchProviders = FXCollections.observableArrayList(
                watchProvidersCache.stream().map(WatchProvider::getProviderName).collect(Collectors.toList())
        );
        watchProvidersField.setItems(watchProviders);
        watchProvidersField.setEditable(false);

        Set<ProductionCompany> productionCompaniesCache = RoomController.fetchProductionCompanies();
        ObservableList<String> productionCompanies = FXCollections.observableArrayList(
                productionCompaniesCache.stream().map(ProductionCompany::getCompanyName).collect(Collectors.toList())
        );
        productionCompaniesField.setItems(productionCompanies);
        productionCompaniesField.setEditable(false);
    }

    @FXML
    @Override
    public void goToHomePageEvent() {
        this.app.showHomePage(activeUser);
    }

    @FXML
    @Override
    public void goToUserPageEvent() {
        this.app.showProfilePage(activeUser);
    }

}
