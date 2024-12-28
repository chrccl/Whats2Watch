package com.whats2watch.w2w.model.dao.dao_factories;

public class PersistenceFactory {

    private PersistenceFactory() {
        throw new UnsupportedOperationException("PersistenceFactory is a utility class and cannot be instantiated.");
    }

    public static DAOFactory createDAO(PersistenceType persistenceType) {
        switch (persistenceType){
            case DATABASE:
                return new DAODatabaseFactory();
            case FILESYSTEM:
                return new DAOFileSystemFactory();
            default:
                return new DAODemoFactory();
        }
    }
}
