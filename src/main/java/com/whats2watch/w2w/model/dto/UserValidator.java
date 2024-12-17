package com.whats2watch.w2w.model.dto;

import com.whats2watch.w2w.model.dto.beans.UserBean;

import java.util.regex.Pattern;

import static com.whats2watch.w2w.model.dto.LoginValidator.checkEmailAndPasswordFormat;

public class UserValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$");

    public static ValidationResult validate(UserBean user) {
        ValidationResult result = new ValidationResult();

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            result.addError("Name must not be empty.");
        }

        if (user.getSurname() == null || user.getSurname().trim().isEmpty()) {
            result.addError("Surname must not be empty.");
        }

        if (user.getGender() == null) {
            result.addError("Gender must not be null.");
        }

        return checkEmailAndPasswordFormat(user, result, EMAIL_PATTERN, PASSWORD_PATTERN);
    }
}
