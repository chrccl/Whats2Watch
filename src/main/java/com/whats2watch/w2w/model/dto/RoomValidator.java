package com.whats2watch.w2w.model.dto;

import com.whats2watch.w2w.model.dto.beans.RoomBean;

import java.time.LocalDate;

public class RoomValidator {

    private RoomValidator() {
        throw new UnsupportedOperationException("RoomValidator is a utility class and cannot be instantiated.");
    }

    public static ValidationResult validate(RoomBean room) {
        ValidationResult result = new ValidationResult();

        if (room.getName() == null || room.getName().trim().isEmpty()) {
            result.addError("Room name must not be empty.");
        }

        if (room.getMediaType() == null) {
            result.addError("Media type must not be null.");
        }

        if (room.getDecade() != null) {
            int currentYear = LocalDate.now().getYear();
            int lastDecade = (currentYear / 10) * 10; // Calculate the last completed decade.
            if(!(room.getDecade() >= 1900 && room.getDecade() <= lastDecade && room.getDecade() % 10 == 0)){
                result.addError("Decade must be a valid year multiple of 10, starting from 1900 to the last completed decade.");
            }
        }

        return result;
    }

}
