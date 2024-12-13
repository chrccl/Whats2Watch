package com.whats2watch.w2w.model.dao.media;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Media;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.dao.DAO;
import com.whats2watch.w2w.model.dao.character.DAODatabaseCharacter;
import com.whats2watch.w2w.model.dao.genre.DAODatabaseGenre;
import com.whats2watch.w2w.model.dao.production_companies.DAODatabaseProductionCompany;
import com.whats2watch.w2w.model.dao.watch_providers.DAODatabaseWatchProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public abstract class DAODatabaseMedia<T extends Media> implements DAO<T, MediaId> {

    protected final Connection conn;

    protected DAODatabaseMedia(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(T entity) throws DAOException {
        saveMedia(entity);
    }

    @Override
    public Set<T> findAll() throws DAOException {
        String sql = "SELECT * FROM " + getTableName();
        Set<T> medias = new HashSet<>();
        try (var stmt = conn.createStatement(); var rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MediaId id = new MediaId(rs.getString("title"), rs.getInt("year"));
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
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey.getTitle());
            stmt.setInt(2, entityKey.getYear());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MediaId id = new MediaId(rs.getString("title"), rs.getInt("year"));
                    T media =  mapRowToMedia(rs);
                    return media;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding movie by ID", e);
        }
    }

    @Override
    public void deleteById(MediaId entityKey) throws DAOException {
        String sql = "DELETE FROM " + getTableName() + " WHERE title = ? AND year = ?";
        try (var stmt = conn.prepareStatement(sql)) {
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
        new DAODatabaseGenre(conn).saveAll(media.getGenres());
        new DAODatabaseCharacter(conn).saveAll(media.getCharacters());
        new DAODatabaseProductionCompany(conn).saveAll(media.getProductionCompanies());
        new DAODatabaseWatchProvider(conn).saveAll(media.getWatchProviders());
    }

    protected void loadAssociations(T.MediaBuilder media, MediaId id) throws DAOException {
        media.genres(new DAODatabaseGenre(conn).findByMovieId(id));
        media.characters(new DAODatabaseCharacter(conn).findByMovieId(id));
        media.productionCompanies(new DAODatabaseProductionCompany(conn).findByMovieId(id));
        media.watchProviders(new DAODatabaseWatchProvider(conn).findByMovieId(id));
    }

    protected abstract void saveMedia(T entity) throws DAOException;

    protected abstract T mapRowToMedia(ResultSet rs) throws DAOException;

    protected abstract String getTableName();

}
