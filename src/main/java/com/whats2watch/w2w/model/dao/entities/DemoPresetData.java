package com.whats2watch.w2w.model.dao.entities;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.Character;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistenceType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DemoPresetData {

    // Production Companies Collection
    public static final Set<ProductionCompany> PRODUCTION_COMPANIES = Set.of(
            new ProductionCompany("Marvel Studios", "https://logo.marvel.com"),
            new ProductionCompany("Pixar Animation Studios", "https://logo.pixar.com"),
            new ProductionCompany("Warner Bros.", "https://logo.warnerbros.com")
    );

    // Watch Providers Collection
    public static final Set<WatchProvider> WATCH_PROVIDERS = Set.of(
            new WatchProvider("Disney+", "https://logo.disneyplus.com"),
            new WatchProvider("Netflix", "https://logo.netflix.com"),
            new WatchProvider("HBO Max", "https://logo.hbomax.com")
    );

    public static final Set<Movie> MOVIES;

    static {
        try {
            MOVIES = PersistanceFactory.createDAO(PersistenceType.FILESYSTEM).createMovieDAO()
                    .findAll().stream()
                    .map(media-> (Movie)media)
                    .collect(Collectors.toSet());
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Set<TVSeries> TVSERIES;

    static {
        try {
            TVSERIES = PersistanceFactory.createDAO(PersistenceType.FILESYSTEM).createTVSeriesDAO()
                    .findAll().stream()
                    .map(media-> (TVSeries)media)
                    .collect(Collectors.toSet());
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

}
