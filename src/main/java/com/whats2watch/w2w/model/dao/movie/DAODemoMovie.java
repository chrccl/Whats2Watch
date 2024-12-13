package com.whats2watch.w2w.model.dao.movie;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.DAO;

import java.util.*;

public class DAODemoMovie implements DAO<Movie, MediaId> {

    private static final Set<Movie> movies = new HashSet<>();

    @Override
    public void save(Movie entity) throws DAOException {
        //TODO
    }

    @Override
    public Movie findById(MediaId entityKey) throws DAOException {
        return null;
    }

    @Override
    public void deleteById(MediaId entityKey) throws DAOException {
        //TODO
    }

    @Override
    public List<Movie> findAll() {
        return new ArrayList<>(movies);
    }
}
