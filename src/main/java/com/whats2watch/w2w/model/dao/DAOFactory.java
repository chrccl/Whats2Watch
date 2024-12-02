package com.whats2watch.w2w.model.dao;

public class DAOFactory {

    private DAOFactory() {
        throw new UnsupportedOperationException("DAOFactory is a utility class and cannot be instantiated.");
    }

    public static <T> GenericDAO<T> getDAO(Class<T> clazz, PersistanceType type) {
        switch (type) {
            case DATABASE:
                return new DatabaseDAO<T>(clazz);
            case FILESYSTEM:
                return new FileSystemDAO<T>(clazz);
            default:
                throw new IllegalArgumentException("PersistanceType not supported: " + type);
        }
    }}
