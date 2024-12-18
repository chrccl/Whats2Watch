package com.whats2watch.w2w.model.dao.dao_factories;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.media.movie.DAODemoMovie;
import com.whats2watch.w2w.model.dao.entities.media.tvseries.DAODemoTVSeries;
import com.whats2watch.w2w.model.dao.entities.room.DAODemoRoom;
import com.whats2watch.w2w.model.dao.entities.user.DAODemoUser;

public class DAODemoFactory implements DAOFactory {

    @Override
    public DAO<Movie, MediaId> createMovieDAO() throws DAOException {
        return DAODemoMovie.getInstance();
    }

    @Override
    public DAO<TVSeries, MediaId> createTVSeriesDAO() throws DAOException {
        return DAODemoTVSeries.getInstance();
    }

    @Override
    public DAO<Room, String> createRoomDAO() throws DAOException {
        return DAODemoRoom.getInstance();
    }

    @Override
    public DAO<User, String> createUserDAO() throws DAOException {
        return DAODemoUser.getInstance();
    }

}
