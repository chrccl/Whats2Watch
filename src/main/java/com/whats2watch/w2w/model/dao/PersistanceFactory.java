package com.whats2watch.w2w.model.dao;

public class PersistanceFactory {

    private PersistanceFactory() {
        throw new UnsupportedOperationException("PersistanceFactory is a utility class and cannot be instantiated.");
    }

    public static DAOFactory createDAO(PersistanceType persistanceType) {
        switch (persistanceType){
            case DATABASE:
                return new DAODatabaseFactory();
            case FILESYSTEM:
                return new DAOFileSystemFactory();
            default:
                return new DAODemoFactory();
        }
    }
}
