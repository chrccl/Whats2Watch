package com.whats2watch.w2w.cli_view;

import com.whats2watch.w2w.controllers.RegisterController;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Dispatcher;
import com.whats2watch.w2w.model.Gender;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dto.LoginValidator;
import com.whats2watch.w2w.model.dto.UserValidator;
import com.whats2watch.w2w.model.dto.ValidationResult;
import com.whats2watch.w2w.model.dto.beans.UserBean;

import java.util.Scanner;

public class RegisterView {

    private static Dispatcher app;

    private static final String ERROR = "Error";

    public static void showMenu(Dispatcher d) {
        if(app == null) app = d;
        Scanner scanner = new Scanner(System.in);
        String action;
        do {
            System.out.println("Type 'L' for Login or 'R' for Register");
            action = scanner.nextLine().toUpperCase();
        }while(!action.equals("L") && !action.equals("R"));
        try {
            if(action.equals("L")) handleLogin();
            else handleRegister();
        } catch (DAOException e) {
            showAlert(ERROR, e.getMessage());
        }
    }

    public static void handleLogin() throws DAOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Login ---");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        UserBean userBean = new UserBean(email, password);
        ValidationResult validatorResult = LoginValidator.validate(userBean);
        if (validatorResult.isValid()) {
            User user = RegisterController.login(userBean);
            if (user != null) {
                System.out.println("Login successful!");
                app.showHomePage(user);
            } else {
                showAlert(ERROR, "Email or Password is incorrect!");
            }
        } else {
            showAlert(ERROR, validatorResult.toString());
        }

    }

    public static void handleRegister() throws DAOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Register ---");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Surname: ");
        String surname = scanner.nextLine().trim();
        System.out.print("Gender (Male/Female/Unknown): ");
        String genderInput = scanner.nextLine().trim().toUpperCase();
        Gender gender;
        try {
            gender = Gender.valueOf(genderInput);
        } catch (IllegalArgumentException e) {
            showAlert(ERROR, "Invalid gender input. Please enter Male or Female or Unknown.");
            return;
        }
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!password.equals(confirmPassword)) {
            showAlert(ERROR, "Password and Confirm Password fields must be equal.");
            return;
        }

        UserBean userBean = new UserBean(name, surname, gender, email, password);
        ValidationResult validatorResult = UserValidator.validate(userBean);
        if (validatorResult.isValid()) {
            RegisterController.register(userBean);
            showAlert("Registration Successful", "Your account has been created!");
            app.showLoginPage();
        } else {
            showAlert(ERROR, validatorResult.toString());
        }

    }

    private static void showAlert(String title, String message) {
        System.err.println(title + ": " + message);
        showMenu(app);
    }

}
