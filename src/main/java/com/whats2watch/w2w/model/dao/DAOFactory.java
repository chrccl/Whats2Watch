package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.exceptions.DAOException;

public class DAOFactory {

    private DAOFactory() {
        throw new UnsupportedOperationException("DAOFactory is a utility class and cannot be instantiated.");
    }

    public static <T, K> DAO<T, K> getDAO(Class<T> clazz, PersistanceType type) throws DAOException {
        switch (type) {
            case DATABASE:
                return new DatabaseDAO<T, K>(clazz);
            case FILESYSTEM:
                return new FileSystemDAO<T, K>(clazz);
            default:
                throw new IllegalArgumentException("PersistanceType not supported: " + type);
        }
    }}
