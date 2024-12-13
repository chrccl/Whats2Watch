package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.media.movie.DAODatabaseMovie;

public class DAODatabaseFactory implements DAOFactory{

    @Override
    public DAO<Movie, MediaId> createMovieDAO() throws DAOException {
        return new DAODatabaseMovie(ConnectionManager.getConnection());
    }

}
