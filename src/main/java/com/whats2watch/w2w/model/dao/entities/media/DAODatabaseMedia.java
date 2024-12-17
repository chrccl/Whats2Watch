package com.whats2watch.w2w.model.dao.entities.media;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.Character;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.character.DAODatabaseCharacter;
import com.whats2watch.w2w.model.dao.entities.genre.DAODatabaseGenre;
import com.whats2watch.w2w.model.dao.entities.production_companies.DAODatabaseProductionCompany;
import com.whats2watch.w2w.model.dao.entities.watch_providers.DAODatabaseWatchProvider;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public abstract class DAODatabaseMedia<T extends Media> implements DAO<T, MediaId> {

    protected final Connection conn;

    private static final String INSERT_INTO = "INSERT INTO ";

    private static final String DELETE_FROM = "DELETE FROM ";

    protected DAODatabaseMedia(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(T entity) throws DAOException {
        saveMedia(entity);
    }

    private void saveMediaGenres(T entity) throws SQLException {
        String deleteQuery = String.format("%s%s%s", DELETE_FROM, getTableName(), "_genres WHERE title = ? AND year = ?");
        try (PreparedStatement deletePs = conn.prepareStatement(deleteQuery)) {
            deletePs.setString(1, entity.getMediaId().getTitle());
            deletePs.setInt(2, entity.getMediaId().getYear());
            deletePs.executeUpdate();
        }

        String insertQuery = INSERT_INTO + getTableName() + "_genres (title, year, genre) VALUES (?, ?, ?)";
        try (PreparedStatement insertPs = conn.prepareStatement(insertQuery)) {
            for (Genre genre : entity.getGenres()) {
                insertPs.setString(1, entity.getMediaId().getTitle());
                insertPs.setInt(2, entity.getMediaId().getYear());
                insertPs.setString(3, genre.name());
                insertPs.addBatch();
            }
            insertPs.executeBatch();
        }
    }

    private void saveMediaCharacters(T entity) throws SQLException {
        String deleteQuery = DELETE_FROM + getTableName() + "_characters WHERE title = ? AND year = ?";
        try (PreparedStatement deletePs = conn.prepareStatement(deleteQuery)) {
            deletePs.setString(1, entity.getMediaId().getTitle());
            deletePs.setInt(2, entity.getMediaId().getYear());
            deletePs.executeUpdate();
        }

        String insertQuery = INSERT_INTO + getTableName() + "_characters (title, year, character) VALUES (?, ?, ?)";
        try (PreparedStatement insertPs = conn.prepareStatement(insertQuery)) {
            for (Character character: entity.getCharacters()) {
                insertPs.setString(1, entity.getMediaId().getTitle());
                insertPs.setInt(2, entity.getMediaId().getYear());
                insertPs.setString(3, character.getCharacterName());
                insertPs.addBatch();
            }
            insertPs.executeBatch();
        }
    }

    private void saveMediaProductionCompanies(T entity) throws SQLException {
        String deleteQuery = DELETE_FROM + getTableName() + "_production_companies WHERE title = ? AND year = ?";
        try (PreparedStatement deletePs = conn.prepareStatement(deleteQuery)) {
            deletePs.setString(1, entity.getMediaId().getTitle());
            deletePs.setInt(2, entity.getMediaId().getYear());
            deletePs.executeUpdate();
        }

        String insertQuery = INSERT_INTO + getTableName() + "_production_companies (title, year, production_company) VALUES (?, ?, ?)";
        try (PreparedStatement insertPs = conn.prepareStatement(insertQuery)){
            for(ProductionCompany productionCompany: entity.getProductionCompanies()){
                insertPs.setString(1, entity.getMediaId().getTitle());
                insertPs.setInt(2, entity.getMediaId().getYear());
                insertPs.setString(3, productionCompany.getCompanyName());
                insertPs.addBatch();
            }
            insertPs.executeBatch();
        }
    }

    private void saveMediaWatchProviders(T entity) throws SQLException {
        String deleteQuery = DELETE_FROM + getTableName() + "_watch_providers WHERE title = ? AND year = ?";
        try (PreparedStatement deletePs = conn.prepareStatement(deleteQuery)) {
            deletePs.setString(1, entity.getMediaId().getTitle());
            deletePs.setInt(2, entity.getMediaId().getYear());
            deletePs.executeUpdate();
        }

        String insertQuery = INSERT_INTO + getTableName() + "_watch_providers (title, year, watch_provider) VALUES (?, ?, ?)";
        try (PreparedStatement insertPs = conn.prepareStatement(insertQuery)){
            for(WatchProvider watchProvider: entity.getWatchProviders()){
                insertPs.setString(1, entity.getMediaId().getTitle());
                insertPs.setInt(2, entity.getMediaId().getYear());
                insertPs.setString(3, watchProvider.getProviderName());
                insertPs.addBatch();
            }
            insertPs.executeBatch();
        }
    }

    @Override
    public Set<T> findAll() throws DAOException {
        String sql = "SELECT * FROM " + getTableName();
        Set<T> medias = new HashSet<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                T media =  mapRowToMedia(rs);
                medias.add(media);
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all medias", e);
        }
        return medias;
    }

    @Override
    public T findById(MediaId entityKey) throws DAOException {
        String sql = "SELECT * FROM " +  getTableName() + " WHERE title = ? AND year = ?";
        T media = null;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey.getTitle());
            stmt.setInt(2, entityKey.getYear());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    media = mapRowToMedia(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding movie by ID", e);
        }
        return media;
    }

    @Override
    public void deleteById(MediaId entityKey) throws DAOException {
        String sql = DELETE_FROM + getTableName() + " WHERE title = ? AND year = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey.getTitle());
            stmt.setInt(2, entityKey.getYear());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error deleting entity by ID", e);
        }
    }

    protected void setCommonAttributes(PreparedStatement stmt, T entity) throws DAOException{
        try{
            stmt.setString(1, entity.getMediaId().getTitle());
            stmt.setString(2, entity.getPlot());
            stmt.setString(3, entity.getPosterUrl());
            stmt.setString(4, entity.getVideoUrl());
            stmt.setDouble(5, entity.getPopularity());
            stmt.setDouble(6, entity.getVoteAverage());
            stmt.setInt(7, entity.getMediaId().getYear());
        }catch(SQLException e){
            throw new DAOException("Error saving common attributes of medias");
        }
    }

    protected void saveAssociations(T media) throws DAOException {
        try{
            new DAODatabaseGenre(conn).saveAll(media.getGenres());
            saveMediaGenres(media);
            new DAODatabaseCharacter(conn).saveAll(media.getCharacters());
            saveMediaCharacters(media);
            new DAODatabaseProductionCompany(conn).saveAll(media.getProductionCompanies());
            saveMediaProductionCompanies(media);
            new DAODatabaseWatchProvider(conn).saveAll(media.getWatchProviders());
            saveMediaWatchProviders(media);
        }catch(SQLException e){
            throw new DAOException("Error saving associations JT of medias");
        }
    }

    protected void loadAssociations(T.MediaBuilder<?> media, MediaId id) throws DAOException {
        media.genres(
                media instanceof Movie.MediaBuilder ?
                    new DAODatabaseGenre(conn).findByMovieId(id) :
                    new DAODatabaseGenre(conn).findByTVSeriesId(id)
        );
        media.characters(
                media instanceof Movie.MediaBuilder ?
                    new DAODatabaseCharacter(conn).findByMovieId(id) :
                    new DAODatabaseCharacter(conn).findByTVSeriesId(id)
        );
        media.productionCompanies(
                media instanceof Movie.MediaBuilder ?
                new DAODatabaseProductionCompany(conn).findByMovieId(id) :
                new DAODatabaseProductionCompany(conn).findByTVSeriesId(id)
        );
        media.watchProviders(
                media instanceof Movie.MediaBuilder ?
                new DAODatabaseWatchProvider(conn).findByMovieId(id) :
                new DAODatabaseWatchProvider(conn).findByTVSeriesId(id)
        );
    }

    protected abstract void saveMedia(T entity) throws DAOException;

    protected abstract T mapRowToMedia(ResultSet rs) throws DAOException;

    protected abstract String getTableName();

}
