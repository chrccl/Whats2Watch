package com.whats2watch.w2w.model.dao;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class FileSystemManager {

    private static volatile ObjectMapper objectMapper;
    private static volatile String BASE_DIRECTORY;

    private FileSystemManager() {}

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            synchronized (FileSystemManager.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                }
            }
        }
        return objectMapper;
    }

    public static String getBaseDirectory() {
        if (BASE_DIRECTORY == null) {
            synchronized (FileSystemManager.class) {
                if (BASE_DIRECTORY == null) {
                    BASE_DIRECTORY = "TO_DEFINE";  // Define your base directory path here
                    File directory = new File(BASE_DIRECTORY);
                    if (!directory.exists()) {
                        directory.mkdirs();  // Ensure the directory exists
                    }
                }
            }
        }
        return BASE_DIRECTORY;
    }
}
