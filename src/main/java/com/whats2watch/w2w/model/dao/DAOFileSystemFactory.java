package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.movie.DAOFileSystemMovie;

public class DAOFileSystemFactory implements DAOFactory{

    @Override
    public DAO<Movie, Integer> createMovieDAO() {
        return new DAOFileSystemMovie(FileSystemManager.getObjectMapper());
    }
}
