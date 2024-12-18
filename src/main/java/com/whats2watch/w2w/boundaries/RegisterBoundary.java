package com.whats2watch.w2w.boundaries;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.controllers.RegisterController;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceType;
import com.whats2watch.w2w.model.dto.LoginValidator;
import com.whats2watch.w2w.model.dto.ValidationResult;
import com.whats2watch.w2w.model.dto.beans.UserBean;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterBoundary {

    private WhatsToWatch app;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    public void setMainApp(WhatsToWatch app) {
        this.app = app;
    }

    @FXML
    private void handleLogin() throws DAOException, IOException {
        ValidationResult validatorResult = LoginValidator.validate(new UserBean(emailField.getText(), passwordField.getText()));
        if(validatorResult.isValid()){
            User user = RegisterController.login(emailField.getText(), passwordField.getText());
            if(user != null){
                this.app.showHomePage(user);
            }else{
                errorLabel.setText("Email o Password non sono corretti!");
            }
        }else{
            errorLabel.setText(validatorResult.toString());
        }
    }

    @FXML
    private void handleGoToRegisterPageEvent() throws IOException {
        this.app.showRegisterPage();
    }

    @FXML
    private void handleGoToLoginPageEvent() throws IOException {
        this.app.showLoginPage();
    }
}
