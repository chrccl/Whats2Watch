package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.exceptions.DAOException;

public interface DAO<T> {
    Boolean save(T entity) throws DAOException;
    T findById(T entityId) throws DAOException;
    Boolean deleteById(T entityId) throws DAOException;
    Boolean updateById(T entityId) throws DAOException, IllegalAccessException;
}
