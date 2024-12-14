package com.whats2watch.w2w.model.dao.entities.user;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.Gender;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DAODatabaseUser implements DAO<User, String> {

    private final Connection conn;

    public DAODatabaseUser(Connection connection) {
        this.conn = connection;
    }

    @Override
    public void save(User entity) throws DAOException {
        String query = "INSERT INTO users (email, name, surname, gender, password) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name = VALUES(name), surname = VALUES(surname), gender = VALUES(gender), password = VALUES(password);";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, entity.getEmail());
            ps.setString(2, entity.getName());
            ps.setString(3, entity.getSurname());
            ps.setString(4, entity.getGender().name());
            ps.setString(5, entity.getPassword());
            ps.executeUpdate();
        }catch (SQLException e) {
            throw new DAOException("Error saving user", e);
        }
    }

    @Override
    public User findById(String entityKey) throws DAOException {
        String query = "SELECT * FROM users WHERE email = ?";
        User user = null;

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, entityKey);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getString("name"),
                        rs.getString("surname"),
                        Gender.valueOf(rs.getString("gender")),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
        }catch (SQLException e){
            throw new DAOException("Error finding user by email", e);
        }

        return user;
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        String query = "DELETE FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, entityKey);
            ps.executeUpdate();
        }catch (SQLException e) {
            throw new DAOException("Error deleting user by email", e);
        }
    }

    @Override
    public Set<User> findAll() throws DAOException {
        String query = "SELECT * FROM users";
        Set<User> users = new HashSet<>();

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getString("name"),
                        rs.getString("surname"),
                        Gender.valueOf(rs.getString("gender")),
                        rs.getString("email"),
                        rs.getString("password")
                );
                users.add(user);
            }
        }catch (SQLException e) {
            throw new DAOException("Error finding all users", e);
        }

        return users;
    }
}
