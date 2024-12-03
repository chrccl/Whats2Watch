package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.exceptions.DAOException;

public class DAOFactory {

    private DAOFactory() {
        throw new UnsupportedOperationException("DAOFactory is a utility class and cannot be instantiated.");
    }

    public static <T, ID> DAO<T, ID> getDAO(Class<T> clazz, PersistanceType type) throws DAOException {
        switch (type) {
            case DATABASE:
                return new DatabaseDAO<T, ID>(clazz);
            case FILESYSTEM:
                return new FileSystemDAO<T, ID>(clazz);
            default:
                throw new IllegalArgumentException("PersistanceType not supported: " + type);
        }
    }}
