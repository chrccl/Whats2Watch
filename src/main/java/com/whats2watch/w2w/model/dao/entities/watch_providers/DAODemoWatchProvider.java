package com.whats2watch.w2w.model.dao.entities.watch_providers;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.WatchProvider;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.DemoPresetData;

import java.util.HashSet;
import java.util.Set;

public class DAODemoWatchProvider implements DAO<WatchProvider, String> {

    private static Set<WatchProvider> watchProviders;
    private static DAODemoWatchProvider instance;

    private DAODemoWatchProvider() {}

    public static synchronized DAODemoWatchProvider getInstance() {
        if (instance == null) {
            instance = new DAODemoWatchProvider();
            watchProviders = new HashSet<>(DemoPresetData.WATCH_PROVIDERS);
        }
        return instance;
    }

    @Override
    public void save(WatchProvider entity) throws DAOException {
        watchProviders.add(entity);
    }

    @Override
    public WatchProvider findById(String entityKey) throws DAOException {
        return watchProviders.stream()
                .filter(watchProvider -> watchProvider.getProviderName().equals(entityKey))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        WatchProvider user = findById(entityKey);
        if (user != null)
            watchProviders.remove(findById(entityKey));
    }

    @Override
    public Set<WatchProvider> findAll() throws DAOException  {
        return watchProviders;
    }

}
