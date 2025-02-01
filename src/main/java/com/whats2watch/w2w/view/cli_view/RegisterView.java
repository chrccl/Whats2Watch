package com.whats2watch.w2w.view.cli_view;

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

import java.util.Scanner;

public class RegisterView {

    private static Dispatcher app;

    private static final String ERROR = "Error";

    private RegisterView() {
        throw new UnsupportedOperationException("RegisterView is a utility class and cannot be instantiated.");
    }

    public static void showMenu(Dispatcher d) {
        if(app == null) app = d;
        Scanner scanner = new Scanner(System.in);
        String action;
        do {
            System.out.println("Type 'L' for Login or 'R' for Register");
            action = scanner.nextLine().toUpperCase();
        }while(!action.equals("L") && !action.equals("R"));
        if(action.equals("L"))
            handleLogin();
        else
            handleRegister();
    }

    public static void handleLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Login ---");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        UserBean userBean = new UserBean(email, password);
        ValidationResult validatorResult = LoginValidator.validate(userBean);
        if (validatorResult.isValid()) {
            try{
                User user = RegisterController.login(userBean);
                System.out.println("Login successful!");
                app.showHomePage(user);
            } catch (EntityNotFoundException e) {
                showAlert(ERROR, "Email or Password is incorrect!");
            }
        } else {
            showAlert(ERROR, validatorResult.toString());
        }

    }

    public static void handleRegister() {
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
            try{
                RegisterController.register(userBean);
                showAlert("Registration Successful", "Your account has been created!");
                app.showLoginPage();
            } catch (EntityCannotBePersistedException e) {
                showAlert(ERROR, "Internal error, please try again.");
            }
        } else {
            showAlert(ERROR, validatorResult.toString());
        }

    }

    private static void showAlert(String title, String message) {
        System.err.println(title + ": " + message);
        showMenu(app);
    }

}
