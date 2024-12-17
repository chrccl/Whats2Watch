package com.whats2watch.w2w.model.dao.entities.media.movie;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.util.*;

public class DAODemoMovie implements DAO<Movie, MediaId> {

    private static final Set<Movie> movies = new HashSet<>();

    @Override
    public void save(Movie entity) throws DAOException {
        movies.add(entity);
    }

    @Override
    public Movie findById(MediaId entityKey) throws DAOException {
        return movies.stream()
                .filter(movie -> movie.getMediaId().equals(entityKey))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteById(MediaId entityKey) throws DAOException {
        Movie movie = findById(entityKey);
        if (movie != null)
            movies.remove(findById(entityKey));
    }

    @Override
    public Set<Movie> findAll() throws DAOException  {
        return movies;
    }
}
