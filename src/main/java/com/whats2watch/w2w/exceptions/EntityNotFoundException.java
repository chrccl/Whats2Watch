package com.whats2watch.w2w.exceptions;

public class EntityNotFoundException extends DAOException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
