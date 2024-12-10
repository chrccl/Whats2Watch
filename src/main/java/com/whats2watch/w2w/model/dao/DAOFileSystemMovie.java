package com.whats2watch.w2w.model.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Movie;

import java.util.List;

public class DAOFileSystemMovie implements DAO<Movie, Integer>{

    private ObjectMapper mapper = new ObjectMapper();

    public DAOFileSystemMovie(ObjectMapper mapper) {
        this.mapper = mapper;
    }

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
        return List.of();
    }
}
