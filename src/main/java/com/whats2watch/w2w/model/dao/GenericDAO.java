package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.exceptions.DAOException;

import java.util.Map;

public interface GenericDAO<T> {
    Boolean save(T entity) throws DAOException;
    T findById(Map<String, Object> compositeKey) throws DAOException;
    Boolean delete(Map<String, Object> compositeKey) throws DAOException;
}
