package com.whats2watch.w2w.model.dao;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileSystemManager {

    private static volatile ObjectMapper objectMapper;
    private static final String BASE_DIRECTORY = "./data/";

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

}
