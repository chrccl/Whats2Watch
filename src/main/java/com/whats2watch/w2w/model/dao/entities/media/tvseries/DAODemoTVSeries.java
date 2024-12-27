package com.whats2watch.w2w.model.dao.entities.media.tvseries;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.TVSeries;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.DemoPresetData;

import java.util.HashSet;
import java.util.Set;

public class DAODemoTVSeries implements DAO<TVSeries, MediaId> {

    private static Set<TVSeries> tvSeries;
    private static DAODemoTVSeries instance;

    private DAODemoTVSeries() {}

    public static synchronized DAODemoTVSeries getInstance() {
        if (instance == null) {
            instance = new DAODemoTVSeries();
            tvSeries = new HashSet<>(DemoPresetData.TV_SERIES);
        }
        return instance;
    }
    @Override
    public void save(TVSeries entity) throws DAOException {
        tvSeries.add(entity);
    }

    @Override
    public TVSeries findById(MediaId entityKey) throws DAOException {
        return tvSeries.stream()
                .filter(tv -> tv.getMediaId().equals(entityKey))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteById(MediaId entityKey) throws DAOException {
        TVSeries tv = findById(entityKey);
        if (tv != null)
            tvSeries.remove(findById(entityKey));
    }

    @Override
    public Set<TVSeries> findAll() throws DAOException  {
        return tvSeries;
    }
}
