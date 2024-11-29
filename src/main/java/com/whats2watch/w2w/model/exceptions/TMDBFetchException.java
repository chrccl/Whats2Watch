package com.whats2watch.w2w.model.exceptions;

public class TMDBFetchException extends RuntimeException {

    public TMDBFetchException(String message) {
        super(message);
    }

    public TMDBFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
