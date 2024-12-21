package com.whats2watch.w2w.model.dao.entities.production_companies;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.ProductionCompany;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DAOFileSystemProductionCompany implements DAO<ProductionCompany, String> {

    private final String filePath;
    private final Map<String, ProductionCompany> prodCompanyStorage;
    private final Gson gson;

    public DAOFileSystemProductionCompany(String filePath) throws DAOException {
        this.filePath = filePath;
        this.prodCompanyStorage = new HashMap<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadFromFile();
    }

    @Override
    public void save(ProductionCompany entity) throws DAOException {
        prodCompanyStorage.put(entity.getCompanyName(), entity);
        saveToFile();
    }

    @Override
    public ProductionCompany findById(String entityKey) throws DAOException {
        ProductionCompany entity = prodCompanyStorage.get(entityKey);
        if (entity == null) {
            throw new DAOException("Production company not found with key: " + entityKey);
        }
        return entity;
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        if (prodCompanyStorage.remove(entityKey) == null) {
            throw new DAOException("Production company not found with key: " + entityKey);
        }
        saveToFile();
    }

    @Override
    public Set<ProductionCompany> findAll() throws DAOException {
        return new HashSet<>(prodCompanyStorage.values());
    }

    protected void loadFromFile() throws DAOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        try (Reader reader = new FileReader(file)) {
            ProductionCompany[] entities = gson.fromJson(reader, ProductionCompany[].class);
            for (ProductionCompany entity : entities) {
                prodCompanyStorage.put(entity.getCompanyName(), entity);
            }
        } catch (IOException e) {
            throw new DAOException("Failed to load data from file: " + e.getMessage(), e);
        }
    }

    protected void saveToFile() throws DAOException {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(prodCompanyStorage.values(), writer);
        } catch (IOException e) {
            throw new DAOException("Failed to save data to file: " + e.getMessage(), e);
        }
    }
}
