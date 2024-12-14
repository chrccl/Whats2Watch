package com.whats2watch.w2w.model.dao.dao_factories;

import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.FileSystemManager;
import com.whats2watch.w2w.model.dao.entities.media.movie.DAOFileSystemMovie;

public class DAOFileSystemFactory implements DAOFactory {

    @Override
    public DAO<Movie, MediaId> createMovieDAO() {
        return new DAOFileSystemMovie(FileSystemManager.getObjectMapper());
    }
}
