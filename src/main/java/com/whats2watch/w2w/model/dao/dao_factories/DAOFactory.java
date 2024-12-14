package com.whats2watch.w2w.model.dao.dao_factories;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.dao.entities.DAO;

public interface DAOFactory {

    <T, K> DAO<T, K> createMovieDAO() throws DAOException;
}
