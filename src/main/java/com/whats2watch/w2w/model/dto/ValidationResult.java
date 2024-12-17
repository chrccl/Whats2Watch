package com.whats2watch.w2w.model.dto;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final List<String> errors = new ArrayList<>();

    public void addError(String error) {
        errors.add(error);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        if (isValid()) {
            return "Validation successful. No errors.";
        } else {
            return "Validation failed. Errors: " + errors;
        }
    }
}
