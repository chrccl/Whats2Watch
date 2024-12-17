package com.whats2watch.w2w.controllers;

import com.whats2watch.w2w.WhatsToWatch;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class RegisterController {

    private WhatsToWatch app;

    public void setMainApp(WhatsToWatch app) {
        this.app = app;
    }

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin(MouseEvent event) {
        System.out.println(emailField.getText());
        System.out.println(passwordField.getText());
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
