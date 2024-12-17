package com.whats2watch.w2w.model.dao.dao_factories;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.entities.ConnectionManager;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.media.movie.DAODatabaseMovie;
import com.whats2watch.w2w.model.dao.entities.media.tvseries.DAODatabaseTVSeries;
import com.whats2watch.w2w.model.dao.entities.room.DAODatabaseRoom;
import com.whats2watch.w2w.model.dao.entities.user.DAODatabaseUser;

public class DAODatabaseFactory implements DAOFactory {

    @Override
    public DAO<Movie, MediaId> createMovieDAO() throws DAOException {
        return new DAODatabaseMovie(ConnectionManager.getConnection());
    }

    @Override
    public DAO<TVSeries, MediaId> createTVSeriesDAO() throws DAOException {
        return new DAODatabaseTVSeries(ConnectionManager.getConnection());
    }

    @Override
    public DAO<Room, String> createRoomDAO() throws DAOException {
        return new DAODatabaseRoom(ConnectionManager.getConnection());
    }

    @Override
    public DAO<User, String> createUserDAO() throws DAOException {
        return new DAODatabaseUser(ConnectionManager.getConnection());
    }


}
