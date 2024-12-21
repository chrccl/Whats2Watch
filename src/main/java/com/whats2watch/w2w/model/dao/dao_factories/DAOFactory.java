package com.whats2watch.w2w.model.dao.dao_factories;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.dao.entities.DAO;

public interface DAOFactory {

    <T, K> DAO<T, K> createMovieDAO() throws DAOException;

    <T, K> DAO<T, K> createTVSeriesDAO() throws DAOException;

    <T, K> DAO<T, K> createRoomDAO() throws DAOException;

    <T, K> DAO<T, K> createUserDAO() throws DAOException;

    <T, K> DAO<T, K> createWatchProviderDAO() throws DAOException;

    <T, K> DAO<T, K> createProductionCompaniesDAO() throws DAOException;

}
