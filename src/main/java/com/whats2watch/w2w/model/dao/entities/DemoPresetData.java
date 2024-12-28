package com.whats2watch.w2w.model.dao.entities;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistenceType;

import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DemoPresetData {

    private static final Logger logger = Logger.getLogger(DemoPresetData.class.getName());


    private DemoPresetData() {
        throw new UnsupportedOperationException("DemoPresetData is a utility class and cannot be instantiated.");
    }

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

    public static final Set<Movie> MOVIES = loadMedia(Movie.class);
    public static final Set<TVSeries> TVSERIES = loadMedia(TVSeries.class);

    private static <T> Set<T> loadMedia(Class<T> mediaClass) {
        Set<T> set = Set.of();
        try {
            var dao = PersistanceFactory.createDAO(PersistenceType.FILESYSTEM);

            if (mediaClass == Movie.class) {
                set = dao.createMovieDAO()
                        .findAll().stream()
                        .map(mediaClass::cast)
                        .collect(Collectors.toSet());
            } else if (mediaClass == TVSeries.class) {
                set = dao.createTVSeriesDAO()
                        .findAll().stream()
                        .map(mediaClass::cast)
                        .collect(Collectors.toSet());
            } else {
                throw new IllegalArgumentException("Unsupported media class: " + mediaClass);
            }
        } catch (DAOException e) {
            logger.severe("Failed to load media: " + e.getMessage());
        }
        return set;
    }

}
