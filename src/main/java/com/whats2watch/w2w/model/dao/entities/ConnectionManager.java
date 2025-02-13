package com.whats2watch.w2w.model.dao.entities;

import com.whats2watch.w2w.config.Config;
import com.whats2watch.w2w.exceptions.DAOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static volatile Connection instance;

    private ConnectionManager() {}

    public static Connection getConnection() throws DAOException {
        if (instance == null) {
            synchronized (ConnectionManager.class) {
                if (instance == null) {
                    try {
                        String url = Config.loadPropertyByName("db.conf.url");
                        String user = Config.loadPropertyByName("db.conf.username");
                        String password = Config.loadPropertyByName("db.conf.password");

                        instance = DriverManager.getConnection(url, user, password);
                    } catch (SQLException e) {
                        throw new DAOException(e.getMessage());
                    }
                }
            }
        }
        return instance;
    }

}
