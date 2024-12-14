package com.whats2watch.w2w.model.dao.entities.media.movie;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.entities.media.DAODatabaseMedia;

import java.sql.*;

public class DAODatabaseMovie extends DAODatabaseMedia<Movie> {

    public DAODatabaseMovie(Connection conn) {
        super(conn);
    }

    @Override
    protected void saveMedia(Movie entity) throws DAOException {
        String sql = "INSERT INTO movies (title, plot, poster_url, video_url, popularity, vote_average, year, director) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE plot = VALUES(plot), " +
                "poster_url = VALUES(poster_url), video_url = VALUES(video_url), popularity = VALUES(popularity), " +
                "vote_average = VALUES(vote_average), director = VALUES(director)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setCommonAttributes(stmt, entity);
            stmt.setString(8, entity.getDirector());
            stmt.executeUpdate();

            saveAssociations(entity);
        } catch (SQLException e) {
            throw new DAOException("Error saving movie-specific data", e);
        }
    }

    @Override
    protected Movie mapRowToMedia(ResultSet rs) throws DAOException {
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
            loadAssociations(builder, id);
        }catch(SQLException e){
            throw new DAOException("Error mapping row to movie", e);
        }
        return builder.build();
    }

    @Override
    protected String getTableName() {
        return "movie";
    }
}


