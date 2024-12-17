package com.whats2watch.w2w.model.dao.entities.media.tvseries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.TVSeries;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DAOFileSystemTVSeries implements DAO<TVSeries, MediaId> {

    private final String filePath;
    private final Map<MediaId, TVSeries> tvSeriesStorage;
    private final Gson gson;

    public DAOFileSystemTVSeries(String filePath) throws DAOException {
        this.filePath = filePath;
        this.tvSeriesStorage = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadFromFile();
    }

    @Override
    public void save(TVSeries entity) throws DAOException {
        tvSeriesStorage.put(entity.getMediaId(), entity);
        saveToFile();
    }

    @Override
    public TVSeries findById(MediaId entityKey) throws DAOException {
        TVSeries tvSeries = tvSeriesStorage.get(entityKey);
        if (tvSeries == null) {
            throw new DAOException("Movie not found with key: " + entityKey);
        }
        return tvSeries;
    }

    @Override
    public void deleteById(MediaId entityKey) throws DAOException {
        if (tvSeriesStorage.remove(entityKey) == null) {
            throw new DAOException("Movie not found with key: " + entityKey);
        }
        saveToFile();
    }

    @Override
    public Set<TVSeries> findAll() throws DAOException {
        return new HashSet<>(tvSeriesStorage.values());
    }

    private void loadFromFile() throws DAOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (Reader reader = new FileReader(file)) {
            TVSeries[] tvSeriess = gson.fromJson(reader, TVSeries[].class);
            for (TVSeries tvSeries : tvSeriess) {
                tvSeriesStorage.put(tvSeries.getMediaId(), tvSeries);
            }
        } catch (IOException e) {
            throw new DAOException("Failed to load data from file: " + e.getMessage(), e);
        }
    }

    private void saveToFile() throws DAOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(tvSeriesStorage.values(), writer);
        } catch (IOException e) {
            throw new DAOException("Failed to save data to file: " + e.getMessage(), e);
        }
    }
}
