package com.whats2watch.w2w.model.dao.entities.media.tvseries;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.TVSeries;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.util.HashSet;
import java.util.Set;

public class DAODemoTVSeries implements DAO<TVSeries, MediaId> {

    private static final Set<TVSeries> tvSeries = new HashSet<>();

    @Override
    public void save(TVSeries entity) throws DAOException {
        tvSeries.add(entity);
    }

    @Override
    public TVSeries findById(MediaId entityKey) throws DAOException {
        return tvSeries.stream()
                .filter(TVSeries -> TVSeries.getMediaId().equals(entityKey))
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
