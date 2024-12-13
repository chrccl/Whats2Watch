package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.movie.DAODemoMovie;

public class DAODemoFactory implements DAOFactory{

    @Override
    public DAO<Movie, MediaId> createMovieDAO() throws DAOException {
        return new DAODemoMovie();
    }
}
