package com.whats2watch.w2w.model.dao.entities.production_companies;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class DAODatabaseProductionCompany implements DAO<ProductionCompany, String> {

    private final Connection conn;

    private static final String COMPANY_NAME = "company_name";

    private static final String LOGO_URL = "logo_url";

    public DAODatabaseProductionCompany(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(ProductionCompany entity) throws DAOException {
        String sql = "INSERT INTO production_companies (company_name, logo_url) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getCompanyName());
            stmt.setString(2, entity.getLogoUrl());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error saving production company", e);
        }
    }

    public void saveAll(Set<ProductionCompany> entities) throws DAOException {
        String sql = "INSERT INTO production_companies (company_name, logo_url) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE logo_url = VALUES(logo_url);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (ProductionCompany entity : entities) {
                stmt.setString(1, entity.getCompanyName());
                stmt.setString(2, entity.getLogoUrl());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new DAOException("Error saving all production companies", e);
        }
    }

    @Override
    public ProductionCompany findById(String entityKey) throws DAOException {
        String sql = "SELECT pc.company_name, pc.logo_url " +
                "FROM production_companies pc WHERE pc.company_name = ?";
        ProductionCompany pc = null;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pc = new ProductionCompany(rs.getString(COMPANY_NAME),
                            rs.getString(LOGO_URL));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding production companies by its name", e);
        }
        return pc;
    }

    public Set<ProductionCompany> findByMovieId(MediaId movieId) throws DAOException {
       return findByMediaId(movieId, Movie.class);
    }

    public Set<ProductionCompany> findByTVSeriesId(MediaId movieId) throws DAOException {
        return findByMediaId(movieId, TVSeries.class);
    }

    private Set<ProductionCompany> findByMediaId(MediaId movieId, Class<? extends Media> clazz) throws DAOException {
        String sql = buildProductionCompanyQueryForMedia(clazz);
        Set<ProductionCompany> companies = new HashSet<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, movieId.getTitle());
            stmt.setInt(2, movieId.getYear());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    companies.add(new ProductionCompany(rs.getString(COMPANY_NAME), rs.getString(LOGO_URL)));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding production companies by movie ID", e);
        }
        return companies;
    }

    private String buildProductionCompanyQueryForMedia(Class<? extends Media> clazz) {
        String sql;
        if(clazz.equals(Movie.class)) {
            sql = "SELECT pc.company_name, pc.logo_url " +
                    "FROM production_companies pc " +
                    "JOIN movie_production_companies mpc ON pc.company_name = mpc.production_company " +
                    "WHERE mpc.title = ? AND mpc.year = ?";
        }else{
            sql = "SELECT pc.company_name, pc.logo_url " +
                    "FROM production_companies pc " +
                    "JOIN tvseries_production_companies mpc ON pc.company_name = mpc.production_company " +
                    "WHERE mpc.title = ? AND mpc.year = ?";
        }
        return sql;
    }

    public Set<ProductionCompany> findByRoomCode(String roomCode) throws DAOException {
        String sql = "SELECT pc.company_name, pc.logo_url " +
                "FROM production_companies pc " +
                "JOIN room_production_company rpc ON pc.company_name = rpc.production_company_name " +
                "WHERE rpc.room_code = ?";
        Set<ProductionCompany> companies = new HashSet<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    companies.add(new ProductionCompany(rs.getString(COMPANY_NAME),
                            rs.getString(LOGO_URL)));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding production companies by movie ID", e);
        }
        return companies;
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        String sql = "DELETE FROM production_companies WHERE company_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entityKey);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error deleting production company by ID", e);
        }
    }

    @Override
    public Set<ProductionCompany> findAll() throws DAOException {
        String sql = "SELECT company_name, logo_url FROM production_companies";
        Set<ProductionCompany> companies = new HashSet<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                companies.add(new ProductionCompany(rs.getString(COMPANY_NAME),
                        rs.getString(LOGO_URL)));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all production companies", e);
        }
        return companies;
    }

}
