package com.whats2watch.w2w.model.dao.dao_factories;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.entities.ConnectionManager;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.media.movie.DAODatabaseMovie;

public class DAODatabaseFactory implements DAOFactory {

    @Override
    public DAO<Movie, MediaId> createMovieDAO() throws DAOException {
        return new DAODatabaseMovie(ConnectionManager.getConnection());
    }

}
