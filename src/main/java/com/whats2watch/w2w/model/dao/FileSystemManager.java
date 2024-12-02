package com.whats2watch.w2w.model.dao;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class FileSystemManager {

    private static volatile ObjectMapper objectMapper;
    private static volatile String baseDirectory;

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
        if (baseDirectory == null) {
            synchronized (FileSystemManager.class) {
                if (baseDirectory == null) {
                    baseDirectory = "TO_DEFINE";  // Define your base directory path here
                    File directory = new File(baseDirectory);
                    if (!directory.exists()) {
                        directory.mkdirs();  // Ensure the directory exists
                    }
                }
            }
        }
        return baseDirectory;
    }
}
