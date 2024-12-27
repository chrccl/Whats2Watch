package com.whats2watch.w2w.model.dao.entities.genre;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DAODatabaseGenre implements DAO<Genre, String> {

    private final Connection conn;

    public DAODatabaseGenre(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(Genre entity) throws DAOException {
        String sql = "INSERT INTO genres (genre_name) VALUES (?) ON DUPLICATE KEY UPDATE genre_name = genre_name;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error saving genre: " + entity.name(), e);
        }
    }

    public void saveAll(Set<Genre> entities) throws DAOException {
        String sql = "INSERT INTO genres (genre_name) VALUES (?) ON DUPLICATE KEY UPDATE genre_name = genre_name;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Genre entity : entities) {
                stmt.setString(1, entity.name());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new DAOException("Error saving all genres", e);
        }
    }

    @Override
    public Genre findById(String entityKey) throws DAOException {
        String sql = "SELECT genre_name FROM genres WHERE genre_name = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Genre.valueOf(rs.getString("genre_name").toUpperCase());
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving genre: " + entityKey, e);
        } catch (IllegalArgumentException e) {
            throw new DAOException("Invalid genre retrieved from database: " + entityKey, e);
        }
        return null;
    }

    public Set<Genre> findByMovieId(MediaId mediaId) throws DAOException {
        return findByMediaId(mediaId, Movie.class);
    }

    public Set<Genre> findByTVSeriesId(MediaId mediaId) throws DAOException {
        return findByMediaId(mediaId, TVSeries.class);
    }

    private Set<Genre> findByMediaId(MediaId movieId, Class<? extends Media> clazz) throws DAOException {
        String sql = generateGenreSqlForMedia(clazz);
        Set<Genre> genres = new HashSet<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movieId.getTitle());
            stmt.setInt(2, movieId.getYear());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(Genre.valueOf(rs.getString("genre_name").toUpperCase()));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving genres for movie ID: " + movieId, e);
        } catch (IllegalArgumentException e) {
            throw new DAOException("Invalid genre found for movie ID: " + movieId, e);
        }

        return genres;
    }

    private String generateGenreSqlForMedia(Class<? extends Media> clazz) {
        String sql;
        if(clazz.equals(Movie.class)) {
            sql = "SELECT g.genre_name " +
                    "FROM genres g " +
                    "INNER JOIN movie_genres mg ON g.genre_name = mg.genre " +
                    "WHERE mg.title = ? AND mg.year = ?;";
        }else{
            sql = "SELECT g.genre_name " +
                    "FROM genres g " +
                    "INNER JOIN tvseries_genres mg ON g.genre_name = mg.genre " +
                    "WHERE mg.title = ? AND mg.year = ?;";
        }
        return sql;
    }

    public Set<Genre> findByRoomCode(String roomCode) throws DAOException {
        String sql = "SELECT g.genre_name " +
            "FROM genres g " +
            "INNER JOIN room_genre rg ON g.genre_name = rg.genre " +
            "WHERE rg.room_code = ?;";
        Set<Genre> genres = new HashSet<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(Genre.valueOf(rs.getString("genre").toUpperCase()));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving genres for room code: " + roomCode, e);
        } catch (IllegalArgumentException e) {
            throw new DAOException("Invalid genre found for room code: " + roomCode, e);
        }

        return genres;
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        String sql = "DELETE FROM genres WHERE genre_name = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error deleting genre: " + entityKey, e);
        }
    }

    @Override
    public Set<Genre> findAll() throws DAOException {
        String sql = "SELECT genre_name FROM genres;";
        Set<Genre> genres = new HashSet<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                genres.add(Genre.valueOf(rs.getString("genre_name").toUpperCase()));
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving all genres", e);
        } catch (IllegalArgumentException e) {
            throw new DAOException("Invalid genre found in database", e);
        }

        return genres;
    }
}
