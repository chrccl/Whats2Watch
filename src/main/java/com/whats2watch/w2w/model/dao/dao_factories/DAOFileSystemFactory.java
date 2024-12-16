package com.whats2watch.w2w.model.dao.dao_factories;

import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.media.movie.DAOFileSystemMovie;

public class DAOFileSystemFactory implements DAOFactory {

    private static final String BASE_DIRECTORY = "src/main/resources/com/whats2watch/w2w/fs_persistence_layer/";

    @Override
    public DAO<Movie, MediaId> createMovieDAO() {
        return new DAOFileSystemMovie(String.format("%s%s", BASE_DIRECTORY, "movies.json"));
    }
}
