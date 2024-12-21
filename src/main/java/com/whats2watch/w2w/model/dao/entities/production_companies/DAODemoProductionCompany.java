package com.whats2watch.w2w.model.dao.entities.production_companies;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.ProductionCompany;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.util.HashSet;
import java.util.Set;

public class DAODemoProductionCompany implements DAO<ProductionCompany, String> {

    private static final Set<ProductionCompany> productionCompanies = new HashSet<>();
    private static DAODemoProductionCompany instance;

    private DAODemoProductionCompany() {}

    public static synchronized DAODemoProductionCompany getInstance() {
        if (instance == null) {
            instance = new DAODemoProductionCompany();
        }
        return instance;
    }

    @Override
    public void save(ProductionCompany entity) throws DAOException {
        productionCompanies.add(entity);
    }

    @Override
    public ProductionCompany findById(String entityKey) throws DAOException {
        return productionCompanies.stream()
                .filter(productionCompany -> productionCompany.getCompanyName().equals(entityKey))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        ProductionCompany user = findById(entityKey);
        if (user != null)
            productionCompanies.remove(findById(entityKey));
    }

    @Override
    public Set<ProductionCompany> findAll() throws DAOException  {
        return productionCompanies;
    }
}
