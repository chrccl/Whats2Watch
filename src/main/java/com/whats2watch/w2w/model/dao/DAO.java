package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.exceptions.DAOException;

import java.util.List;

public interface DAO<T, ID> {
    void save(T entity) throws DAOException;
    T findById(ID entityId) throws DAOException;
    void deleteById(ID entityId) throws DAOException;
    List<T> findAll();
}
