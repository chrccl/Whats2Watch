package com.whats2watch.w2w.model.dto;

import com.whats2watch.w2w.model.dto.beans.UserBean;

import java.util.regex.Pattern;

public class LoginValidator {

    private LoginValidator() {
        throw new UnsupportedOperationException("LoginValidator is a utility class and cannot be instantiated.");
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z\\d]).{8,}$");


    public static ValidationResult validate(UserBean user) {
        ValidationResult result = new ValidationResult();

        return checkEmailAndPasswordFormat(user, result, EMAIL_PATTERN, PASSWORD_PATTERN);
    }

    static ValidationResult checkEmailAndPasswordFormat(UserBean user, ValidationResult result, Pattern emailPattern, Pattern passwordPattern) {
        if (user.getEmail() == null || !emailPattern.matcher(user.getEmail()).matches()) {
            result.addError("Email must be a valid email address.");
        }

        if (user.getPassword() == null || !passwordPattern.matcher(user.getPassword()).matches()) {
            result.addError("Password must be at least 8 characters long, include one number, one uppercase letter, one lowercase letter, and one special character.");
        }

        return result;
    }
}
