package com.whats2watch.w2w.model.dao.movie;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.DAO;

import java.sql.Connection;
import java.util.List;

public class DAODatabaseMovie implements DAO<Movie, Integer> {

    private Connection conn;

    public DAODatabaseMovie(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(Movie entity) throws DAOException {
        //TODO
    }

    @Override
    public Movie findById(Integer entityKey) throws DAOException {
        return null;
    }

    @Override
    public void deleteById(Integer entityKey) throws DAOException {
        //TODO
    }

    @Override
    public List<Movie> findAll() {
        return List.of();
    }
}
