package com.whats2watch.w2w.model.dao.dao_factories;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.media.movie.DAOFileSystemMovie;
import com.whats2watch.w2w.model.dao.entities.media.tvseries.DAOFileSystemTVSeries;
import com.whats2watch.w2w.model.dao.entities.production_companies.DAOFileSystemProductionCompany;
import com.whats2watch.w2w.model.dao.entities.room.DAOFileSystemRoom;
import com.whats2watch.w2w.model.dao.entities.user.DAOFileSystemUser;
import com.whats2watch.w2w.model.dao.entities.watch_providers.DAOFileSystemWatchProvider;

public class DAOFileSystemFactory implements DAOFactory {

    private static final String BASE_DIRECTORY = "src/main/resources/com/whats2watch/w2w/fs_persistence_layer/";

    @Override
    public DAO<Movie, MediaId> createMovieDAO() throws DAOException {
        return new DAOFileSystemMovie(String.format("%s%s", BASE_DIRECTORY, "movies.json"));
    }

    @Override
    public DAO<TVSeries, MediaId> createTVSeriesDAO() throws DAOException {
        return new DAOFileSystemTVSeries(String.format("%s%s", BASE_DIRECTORY, "tvseries.json"));
    }

    @Override
    public DAO<Room, String> createRoomDAO() throws DAOException {
        return new DAOFileSystemRoom(String.format("%s%s", BASE_DIRECTORY, "rooms.json"));
    }

    @Override
    public DAO<User, String> createUserDAO() throws DAOException {
        return new DAOFileSystemUser(String.format("%s%s", BASE_DIRECTORY, "users.json"));
    }

    @Override
    public DAO<WatchProvider, String> createWatchProviderDAO() throws DAOException {
        return new DAOFileSystemWatchProvider(String.format("%s%s", BASE_DIRECTORY, "watch_providers.json"));
    }

    @Override
    public DAO<ProductionCompany, String> createProductionCompaniesDAO() throws DAOException {
        return new DAOFileSystemProductionCompany(String.format("%s%s", BASE_DIRECTORY, "watch_providers.json"));
    }

}
