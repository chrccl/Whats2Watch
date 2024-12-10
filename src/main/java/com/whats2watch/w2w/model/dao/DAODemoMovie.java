package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Movie;

import java.util.*;

public class DAODemoMovie implements DAO<Movie, Integer>{

    private static final Set<Movie> movies = new HashSet<>();

    @Override
    public void save(Movie entity) throws DAOException {

    }

    @Override
    public Movie findById(Integer entityKey) throws DAOException {
        return null;
    }

    @Override
    public void deleteById(Integer entityKey) throws DAOException {

    }

    @Override
    public List<Movie> findAll() {
        return new ArrayList<>(movies);
    }
}
