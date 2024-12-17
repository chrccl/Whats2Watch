package com.whats2watch.w2w.model.dao.entities.actor;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Actor;
import com.whats2watch.w2w.model.Gender;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DAODatabaseActor implements DAO<Actor, String> {

    private final Connection conn;

    public DAODatabaseActor(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(Actor entity) throws DAOException {
        String sql = "INSERT INTO actors (full_name, popularity, gender) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE popularity = VALUES(popularity), gender = VALUES(gender);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getFullName());
            stmt.setDouble(2, entity.getPopularity());
            stmt.setString(3, entity.getGender().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error saving actor", e);
        }
    }

    @Override
    public Actor findById(String entityKey) throws DAOException {
        String sql = "SELECT full_name, popularity, gender FROM actors WHERE full_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Actor(rs.getString("full_name"),
                            rs.getDouble("popularity"),
                            Gender.valueOf(rs.getString("gender").toUpperCase()));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding actor by ID", e);
        }
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        String sql = "DELETE FROM actors WHERE full_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error deleting actor by ID", e);
        }
    }

    @Override
    public Set<Actor> findAll() throws DAOException {
        String sql = "SELECT full_name, popularity, gender FROM actors";
        Set<Actor> actors = new HashSet<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                actors.add(new Actor(rs.getString("full_name"),
                        rs.getDouble("popularity"),
                        Gender.valueOf(rs.getString("gender").toUpperCase())));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all actors", e);
        }
        return actors;
    }
}
