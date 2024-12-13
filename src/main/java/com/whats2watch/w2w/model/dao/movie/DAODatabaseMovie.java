package com.whats2watch.w2w.model.dao.movie;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.DAO;
import com.whats2watch.w2w.model.dao.character.DAODatabaseCharacter;
import com.whats2watch.w2w.model.dao.genre.DAODatabaseGenre;
import com.whats2watch.w2w.model.dao.productionCompanies.DAODatabaseProductionCompany;
import com.whats2watch.w2w.model.dao.watchProviders.DAODatabaseWatchProvider;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DAODatabaseMovie implements DAO<Movie, MediaId> {

    private final Connection conn;

    public DAODatabaseMovie(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(Movie entity) throws DAOException {
        String sql = "INSERT INTO movies (title, plot, poster_url, video_url, popularity, vote_average, year, director) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE plot = VALUES(plot), poster_url = VALUES(poster_url), " +
                "video_url = VALUES(video_url), popularity = VALUES(popularity), vote_average = VALUES(vote_average), year = VALUES(year), director = VALUES(director);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getMediaId().getTitle());
            stmt.setString(2, entity.getPlot());
            stmt.setString(3, entity.getPosterUrl());
            stmt.setString(4, entity.getVideoUrl());
            stmt.setDouble(5, entity.getPopularity());
            stmt.setDouble(6, entity.getVoteAverage());
            stmt.setInt(7, entity.getMediaId().getYear());
            stmt.setString(8, entity.getDirector());
            stmt.executeUpdate();

            saveAssociations(entity);
        } catch (SQLException e) {
            throw new DAOException("Error saving movie", e);
        }
    }

    @Override
    public Movie findById(MediaId entityKey) throws DAOException {
        String sql = "SELECT * FROM movies WHERE title = ? AND year = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey.getTitle());
            stmt.setInt(2, entityKey.getYear());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMovie(rs);
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
        String sql = "DELETE FROM movies WHERE title = ? AND year = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey.getTitle());
            stmt.setInt(2, entityKey.getYear());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error deleting movie by ID", e);
        }
    }

    @Override
    public Set<Movie> findAll() throws DAOException  {
        String sql = "SELECT * FROM movies";
        Set<Movie> movies = new HashSet<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                movies.add(mapRowToMovie(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all movies", e);
        }
        return movies;
    }

    private void saveAssociations(Movie movie) throws DAOException {
        DAODatabaseGenre genreDAO = new DAODatabaseGenre(conn);
        DAODatabaseCharacter characterDAO = new DAODatabaseCharacter(conn);
        DAODatabaseProductionCompany productionCompanyDAO = new DAODatabaseProductionCompany(conn);
        DAODatabaseWatchProvider watchProviderDAO = new DAODatabaseWatchProvider(conn);

        genreDAO.saveAll(movie.getGenres());
        characterDAO.saveAll(movie.getCharacters());
        productionCompanyDAO.saveAll(movie.getProductionCompanies());
        watchProviderDAO.saveAll(movie.getWatchProviders());
    }

    private Movie mapRowToMovie(ResultSet rs) throws DAOException {
        Movie.MediaBuilder builder = new Movie.MediaBuilder();
        try {
            MediaId id = new MediaId(rs.getString("title"), rs.getInt("year"));
            builder.mediaId(id)
                    .plot(rs.getString("plot"))
                    .posterUrl(rs.getString("poster_url"))
                    .videoUrl(rs.getString("video_url"))
                    .popularity(rs.getDouble("popularity"))
                    .voteAverage(rs.getDouble("vote_average"))
                    .director(rs.getString("director"));

            builder.genres(new DAODatabaseGenre(conn).findByMovieId(id));
            builder.characters(new DAODatabaseCharacter(conn).findByMovieId(id));
            builder.productionCompanies(new DAODatabaseProductionCompany(conn).findByMovieId(id));
            builder.watchProviders(new DAODatabaseWatchProvider(conn).findByMovieId(id));
        }catch(SQLException e){
            throw new DAOException("Error mapping row to movie", e);
        }
        return builder.build();
    }
}
