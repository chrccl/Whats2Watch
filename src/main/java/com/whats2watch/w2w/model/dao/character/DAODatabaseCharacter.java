package com.whats2watch.w2w.model.dao.character;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Actor;
import com.whats2watch.w2w.model.Character;
import com.whats2watch.w2w.model.Gender;
import com.whats2watch.w2w.model.MediaId;
import com.whats2watch.w2w.model.dao.DAO;
import com.whats2watch.w2w.model.dao.actor.DAODatabaseActor;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DAODatabaseCharacter implements DAO<Character, String> {

    private final Connection conn;

    public DAODatabaseCharacter(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(Character entity) throws DAOException {
        String sql = "INSERT INTO characters (character_name, actor_name) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE actor_name = VALUES(actor_name);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getCharacterName());
            stmt.setString(2, entity.getActor().getFullName());
            stmt.executeUpdate();

            DAO<Actor, String> actorDAO = new DAODatabaseActor(conn);
            actorDAO.save(entity.getActor());
        } catch (SQLException e) {
            throw new DAOException("Error saving character", e);
        }
    }

    public void saveAll(Set<Character> entities) throws DAOException {
        String sql = "INSERT INTO characters (character_name, actor_name) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE actor_name = VALUES(actor_name);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            DAO<Actor, String> actorDAO = new DAODatabaseActor(conn);
            for (Character entity : entities) {
                actorDAO.save(entity.getActor()); // In order to ensure associated actors are saved
                stmt.setString(1, entity.getCharacterName());
                stmt.setString(2, entity.getActor().getFullName());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new DAOException("Error saving all characters", e);
        }
    }

    @Override
    public Character findById(String entityKey) throws DAOException {
        String sql = "SELECT c.character_name, a.full_name, a.popularity, a.gender " +
                "FROM characters c " +
                "JOIN movie_characters mc ON c.character_name = mc.character_name " +
                "JOIN actors a ON c.actor_name = a.full_name " +
                "WHERE c.character_name = ?;";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey);
            try (ResultSet rs = stmt.executeQuery()) {
                Character character = null;
                if (rs.next()) {
                    Actor actor = new Actor(rs.getString("full_name"),
                            rs.getDouble("popularity"),
                            Gender.valueOf(rs.getString("gender").toUpperCase()));
                    character = new Character(rs.getString("character_name"), actor);
                    return character;
                }else{
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding character by its name", e);
        }
    }

    public Set<Character> findByMovieId(MediaId mediaId) throws DAOException {
        String sql = "SELECT c.character_name, a.full_name, a.popularity, a.gender " +
                "FROM characters c " +
                "JOIN movie_characters mc ON c.character_name = mc.character_name " +
                "JOIN actors a ON c.actor_name = a.full_name " +
                "WHERE mc.title = ? AND mc.year = ?;";
        Set<Character> characters = new HashSet<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mediaId.getTitle());
            stmt.setInt(2, mediaId.getYear());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Actor actor = new Actor(rs.getString("full_name"),
                            rs.getDouble("popularity"),
                            Gender.valueOf(rs.getString("gender").toUpperCase()));
                    characters.add(new Character(rs.getString("character_name"), actor));
                }
                return characters;
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding characters by movie ID", e);
        }
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        String sql = "DELETE FROM characters WHERE character_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error deleting character by ID", e);
        }
    }

    @Override
    public Set<Character> findAll() throws DAOException {
        String sql = "SELECT * FROM characters";
        Set<Character> characters = new HashSet<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String characterName = rs.getString("character_name");
                String actorName = rs.getString("actor_name");

                DAO<Actor, String> actorDAO = new DAODatabaseActor(conn);
                Actor actor = actorDAO.findById(actorName);
                characters.add(new Character(characterName, actor));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all characters", e);
        }
        return characters;
    }
}
