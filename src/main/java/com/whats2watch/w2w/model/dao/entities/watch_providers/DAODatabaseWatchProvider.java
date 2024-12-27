package com.whats2watch.w2w.model.dao.entities.watch_providers;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.sql.*;

import java.util.HashSet;
import java.util.Set;

public class DAODatabaseWatchProvider implements DAO<WatchProvider, String> {

    private final Connection conn;

    private static final String PROVIDER_NAME = "provider_name";

    private static final String LOGO_URL = "logo_url";

    public DAODatabaseWatchProvider(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(WatchProvider entity) throws DAOException {
        String sql = "INSERT INTO watch_providers (provider_name, logo_url) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getProviderName());
            stmt.setString(2, entity.getLogoUrl());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error saving watch provider", e);
        }
    }

    public void saveAll(Set<WatchProvider> entities) throws DAOException {
        String sql = "INSERT INTO watch_providers (provider_name, logo_url) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (WatchProvider entity : entities) {
                stmt.setString(1, entity.getProviderName());
                stmt.setString(2, entity.getLogoUrl());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new DAOException("Error saving all watch providers", e);
        }
    }

    @Override
    public WatchProvider findById(String entityKey) throws DAOException {
        String sql = "SELECT pc.provider_name, pc.logo_url " +
                "FROM watch_providers pc WHERE pc.provider_name = ?";
        WatchProvider wp = null;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    wp = new WatchProvider(rs.getString(PROVIDER_NAME),
                            rs.getString(LOGO_URL));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding watch providers by its name", e);
        }
        return wp;
    }

    public Set<WatchProvider> findByMovieId(MediaId mediaId) throws DAOException {
        return findByMediaId(mediaId, Movie.class);
    }

    public Set<WatchProvider> findByTVSeriesId(MediaId mediaId) throws DAOException {
        return findByMediaId(mediaId, TVSeries.class);
    }

    private Set<WatchProvider> findByMediaId(MediaId mediaId, Class<? extends Media> clazz) throws DAOException {
        String sql = buildWatchProviderSqlForMedia(clazz);
        Set<WatchProvider> providers = new HashSet<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mediaId.getTitle());
            stmt.setInt(2, mediaId.getYear());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    providers.add(new WatchProvider(rs.getString(PROVIDER_NAME), rs.getString(LOGO_URL)));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding watch providers by movie ID", e);
        }
        return providers;
    }

    private String buildWatchProviderSqlForMedia(Class<? extends Media> clazz) {
        String sql;
        if(clazz.equals(Media.class)) {
            sql = "SELECT wp.provider_name, wp.logo_url " +
                    "FROM watch_providers wp " +
                    "JOIN movie_watch_providers mwp ON wp.provider_name = mwp.watch_provider " +
                    "WHERE mwp.title = ? AND mwp.year = ?";
        }else{
            sql = "SELECT wp.provider_name, wp.logo_url " +
                    "FROM watch_providers wp " +
                    "JOIN tvseries_watch_providers mwp ON wp.provider_name = mwp.watch_provider " +
                    "WHERE mwp.title = ? AND mwp.year = ?";
        }
        return sql;
    }

    public Set<WatchProvider> findByRoomCode(String roomCode) throws DAOException {
        String sql = "SELECT wp.provider_name, wp.logo_url " +
                "FROM watch_providers wp " +
                "JOIN room_watch_provider rwp ON wp.provider_name = rwp.watch_provider_name " +
                "WHERE rwp.room_code = ?";
        Set<WatchProvider> providers = new HashSet<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    providers.add(new WatchProvider(rs.getString(PROVIDER_NAME),
                            rs.getString(LOGO_URL)));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding watch providers by movie ID", e);
        }
        return providers;
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        String sql = "DELETE FROM watch_providers WHERE provider_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error deleting watch provider by ID", e);
        }
    }

    @Override
    public Set<WatchProvider> findAll() throws DAOException {
        String sql = "SELECT provider_name, logo_url FROM watch_providers";
        Set<WatchProvider> providers = new HashSet<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                providers.add(new WatchProvider(rs.getString(PROVIDER_NAME),
                        rs.getString(LOGO_URL)));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all watch providers", e);
        }
        return providers;
    }
}
