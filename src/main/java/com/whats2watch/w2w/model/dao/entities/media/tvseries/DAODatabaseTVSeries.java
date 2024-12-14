package com.whats2watch.w2w.model.dao.entities.media.tvseries;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.TVSeries;
import com.whats2watch.w2w.model.dao.entities.media.DAODatabaseMedia;

import java.sql.*;

public class DAODatabaseTVSeries extends DAODatabaseMedia<TVSeries> {

    public DAODatabaseTVSeries(Connection conn) {
        super(conn);
    }

    @Override
    protected void saveMedia(TVSeries entity) throws DAOException {
        String sql = "INSERT INTO tvseries (title, plot, poster_url, video_url, popularity, vote_average, year, number_of_seasons, number_of_episodes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE plot = VALUES(plot), poster_url = VALUES(poster_url), " +
                "video_url = VALUES(video_url), popularity = VALUES(popularity), vote_average = VALUES(vote_average)," +
                "number_of_seasons = VALUES(number_of_seasons), number_of_episodes = VALUES(number_of_episodes);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setCommonAttributes(stmt, entity);
            stmt.setInt(8, entity.getNumberOfSeasons());
            stmt.setInt(9, entity.getNumberOfEpisodes());
            stmt.executeUpdate();

            saveAssociations(entity);
        } catch (SQLException e) {
            throw new DAOException("Error saving movie", e);
        }
    }

    @Override
    protected TVSeries mapRowToMedia(ResultSet rs) throws DAOException {
        TVSeries.MediaBuilder builder = new TVSeries.MediaBuilder();
        try {
            MediaId id = new MediaId(rs.getString("title"), rs.getInt("year"));
            builder.mediaId(id)
                    .plot(rs.getString("plot"))
                    .posterUrl(rs.getString("poster_url"))
                    .videoUrl(rs.getString("video_url"))
                    .popularity(rs.getDouble("popularity"))
                    .voteAverage(rs.getDouble("vote_average"))
                    .numberOfSeasons(rs.getInt("number_of_seasons"))
                    .numberOfEpisodes(rs.getInt("number_of_episodes"));
            loadAssociations(builder, id);
        }catch(SQLException e){
            throw new DAOException("Error mapping row to movie", e);
        }
        return builder.build();
    }

    @Override
    protected String getTableName() {
        return "tvseries";
    }
}
