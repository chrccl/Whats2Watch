package com.whats2watch.w2w.model.dao.entities.media.movie;

import com.google.gson.reflect.TypeToken;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.entities.media.DAOFileSystemMedia;

public class DAOFileSystemMovie extends DAOFileSystemMedia<Movie> {

    public DAOFileSystemMovie(String filePath) throws DAOException {
        super(filePath, new TypeToken<Movie[]>() {}.getType());
    }

    @Override
    protected String getEntityName() {
        return "Movie";
    }
}
