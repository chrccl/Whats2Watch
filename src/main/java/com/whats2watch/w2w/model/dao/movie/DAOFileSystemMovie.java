package com.whats2watch.w2w.model.dao.movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.DAO;

import java.util.List;

public class DAOFileSystemMovie implements DAO<Movie, MediaId> {

    private ObjectMapper mapper = new ObjectMapper();

    public DAOFileSystemMovie(ObjectMapper mapper) {
        this.mapper = mapper;
    }

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
        return List.of();
    }
}
