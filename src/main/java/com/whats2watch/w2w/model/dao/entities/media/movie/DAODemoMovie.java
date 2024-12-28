package com.whats2watch.w2w.model.dao.entities.media.movie;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.DemoPresetData;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class DAODemoMovie implements DAO<Movie, MediaId> {

    private static Set<Movie> movies;
    private static DAODemoMovie instance;

    private DAODemoMovie() {}

    public static synchronized DAODemoMovie getInstance() {
        if (instance == null) {
            instance = new DAODemoMovie();
            movies = DemoPresetData.MOVIES;
        }
        return instance;
    }
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
        return movies
                .stream()
                .sorted((entry1, entry2) ->
                        Integer.compare(entry2.getMediaId().getYear(), entry1.getMediaId().getYear()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
