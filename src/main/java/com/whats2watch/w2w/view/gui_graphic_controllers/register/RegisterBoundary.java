package com.whats2watch.w2w.view.gui_graphic_controllers.register;

import com.whats2watch.w2w.controllers.RegisterController;
import com.whats2watch.w2w.exceptions.EntityCannotBePersistedException;
import com.whats2watch.w2w.exceptions.EntityNotFoundException;
import com.whats2watch.w2w.model.Dispatcher;
import com.whats2watch.w2w.model.Gender;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dto.LoginValidator;
import com.whats2watch.w2w.model.dto.UserValidator;
import com.whats2watch.w2w.model.dto.ValidationResult;
import com.whats2watch.w2w.model.dto.beans.UserBean;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterBoundary implements RegisterBoundaryInOp, RegisterBoundaryOutOp {

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
    @Override
    public void handleLogin() {
        UserBean userBean = new UserBean(emailField.getText(), passwordField.getText());
        ValidationResult validatorResult = LoginValidator.validate(userBean);
        if(validatorResult.isValid()){
            try{
                User user = RegisterController.login(userBean);
                this.app.showHomePage(user);
            }catch (EntityNotFoundException e){
                showAlert(Alert.AlertType.ERROR, ERROR, "Email o Password non sono corretti!");
            }
        }else{
            showAlert(Alert.AlertType.ERROR, ERROR, validatorResult.toString());
        }
    }

    @FXML
    @Override
    public void handleRegister() {
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
                try{
                    RegisterController.register(userBean);
                    showAlert(Alert.AlertType.CONFIRMATION, "Registration Successful", "Your account has been created!");
                    clearForm();
                    this.app.showLoginPage();
                }catch (EntityCannotBePersistedException e){
                    showAlert(Alert.AlertType.ERROR, ERROR, "Internal Error, please try again!");
                }
            }else{
                showAlert(Alert.AlertType.ERROR, ERROR, validatorResult.toString());
            }
        }else{
            showAlert(Alert.AlertType.ERROR, ERROR, "Password and Confirm Password field must be equals.");
        }
    }

    @FXML
    @Override
    public void handleGoToRegisterPageEvent() {
        this.app.showRegisterPage();
    }

    @FXML
    @Override
    public void handleGoToLoginPageEvent() {
        this.app.showLoginPage();
    }

    @Override
    public void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void clearForm() {
        nameField.clear();
        surnameField.clear();
        genderChoice.setValue("Select gender");
        registerEmailField.clear();
        registerPasswordField.clear();
        confirmPasswordField.clear();
    }
}
