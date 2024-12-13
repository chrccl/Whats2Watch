package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.media.movie.DAOFileSystemMovie;

public class DAOFileSystemFactory implements DAOFactory{

    @Override
    public DAO<Movie, MediaId> createMovieDAO() {
        return new DAOFileSystemMovie(FileSystemManager.getObjectMapper());
    }
}
