package com.whats2watch.w2w.model.dao;

public class PersistanceFactory {

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
