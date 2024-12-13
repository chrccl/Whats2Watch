package com.whats2watch.w2w.model.dao.movie;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.Movie;
import com.whats2watch.w2w.model.dao.DAO;

import java.sql.Connection;
import java.util.List;

public class DAODatabaseMovie implements DAO<Movie, MediaId> {

    private Connection conn;

    public DAODatabaseMovie(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(Movie entity) throws DAOException {
        String sql = "INSERT INTO movies (id, title, plot, poster_url, video_url, popularity, vote_average, year, director) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE title = VALUES(title), plot = VALUES(plot), poster_url = VALUES(poster_url), " +
                "video_url = VALUES(video_url), popularity = VALUES(popularity), vote_average = VALUES(vote_average), year = VALUES(year), director = VALUES(director);";

    }

    @Override
    public Movie findById(MediaId entityKey) throws DAOException {
        return null;
    }

    @Override
    public void deleteById(MediaId entityKey) throws DAOException {
        //TODO
    }

    @Override
    public List<Movie> findAll() {
        return List.of();
    }
}
