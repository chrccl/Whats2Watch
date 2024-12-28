package com.whats2watch.w2w.gui_graphic_controllers;

import com.whats2watch.w2w.controllers.RegisterController;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Dispatcher;
import com.whats2watch.w2w.model.Gender;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dto.LoginValidator;
import com.whats2watch.w2w.model.dto.UserValidator;
import com.whats2watch.w2w.model.dto.ValidationResult;
import com.whats2watch.w2w.model.dto.beans.UserBean;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterBoundary {

    private Dispatcher app;

    private static final String ERROR = "Error";

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private ChoiceBox<String> genderChoice;

    @FXML
    private TextField registerEmailField;

    @FXML
    private PasswordField registerPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    public void setMainApp(Dispatcher app) {
        this.app = app;
    }

    @FXML
    private void handleLogin() throws DAOException {
        UserBean userBean = new UserBean(emailField.getText(), passwordField.getText());
        ValidationResult validatorResult = LoginValidator.validate(userBean);
        if(validatorResult.isValid()){
            User user = RegisterController.login(userBean);
            if(user != null){
                this.app.showHomePage(user);
            }else{
                showAlert(Alert.AlertType.ERROR, ERROR, "Email o Password non sono corretti!");
            }
        }else{
            showAlert(Alert.AlertType.ERROR, ERROR, validatorResult.toString());
        }
    }

    @FXML
    private void handleRegister() throws DAOException {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        Gender gender = Gender.valueOf(genderChoice.getValue().toUpperCase());
        String email = registerEmailField.getText().trim();
        String password = registerPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        UserBean userBean = new UserBean(name, surname, gender, email, password);
        if(password.equals(confirmPassword)){
            ValidationResult validatorResult = UserValidator.validate(userBean);
            if(validatorResult.isValid()){
                RegisterController.register(userBean);
                showAlert(Alert.AlertType.CONFIRMATION, "Registration Successful", "Your account has been created!");
                clearForm();
                this.app.showLoginPage();
            }else{
                showAlert(Alert.AlertType.ERROR, ERROR, validatorResult.toString());
            }
        }else{
            showAlert(Alert.AlertType.ERROR, ERROR, "Password and Confirm Password field must be equals.");
        }
    }

    @FXML
    private void handleGoToRegisterPageEvent() {
        this.app.showRegisterPage();
    }

    @FXML
    private void handleGoToLoginPageEvent() {
        this.app.showLoginPage();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        nameField.clear();
        surnameField.clear();
        genderChoice.setValue("Select gender");
        registerEmailField.clear();
        registerPasswordField.clear();
        confirmPasswordField.clear();
    }
}
