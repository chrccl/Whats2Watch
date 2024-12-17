package com.whats2watch.w2w.model.dao.entities.media.movie;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DAOFileSystemMovie implements DAO<Movie, MediaId> {

    private final String filePath;
    private final Map<MediaId, Movie> movieStorage;
    private final Gson gson;

    public DAOFileSystemMovie(String filePath) throws DAOException {
        this.filePath = filePath;
        this.movieStorage = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadFromFile();
    }

    @Override
    public void save(Movie entity) throws DAOException {
        movieStorage.put(entity.getMediaId(), entity);
        saveToFile();
    }

    @Override
    public Movie findById(MediaId entityKey) throws DAOException {
        Movie movie = movieStorage.get(entityKey);
        if (movie == null) {
            throw new DAOException("Movie not found with key: " + entityKey);
        }
        return movie;
    }

    @Override
    public void deleteById(MediaId entityKey) throws DAOException {
        if (movieStorage.remove(entityKey) == null) {
            throw new DAOException("Movie not found with key: " + entityKey);
        }
        saveToFile();
    }

    @Override
    public Set<Movie> findAll() throws DAOException {
        return new HashSet<>(movieStorage.values());
    }

    private void loadFromFile() throws DAOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return; // No file to load from yet.
        }
        try (Reader reader = new FileReader(file)) {
            Movie[] movies = gson.fromJson(reader, Movie[].class);
            for (Movie movie : movies) {
                movieStorage.put(movie.getMediaId(), movie);
            }
        } catch (IOException e) {
            throw new DAOException("Failed to load data from file: " + e.getMessage(), e);
        }
    }

    private void saveToFile() throws DAOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(movieStorage.values(), writer);
        } catch (IOException e) {
            throw new DAOException("Failed to save data to file: " + e.getMessage(), e);
        }
    }

}
