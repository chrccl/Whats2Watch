package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.exceptions.DAOException;

public interface DAOFactory {

    <T, K> DAO<T, K> createMovieDAO() throws DAOException;
}
