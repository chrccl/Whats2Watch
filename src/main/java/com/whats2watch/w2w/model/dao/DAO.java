package com.whats2watch.w2w.model.dao;

import com.whats2watch.w2w.exceptions.DAOException;

import java.util.List;

public interface DAO<T, K> {
    void save(T entity) throws DAOException;
    T findById(K entityKey) throws DAOException;
    void deleteById(K entityKey) throws DAOException;
    List<T> findAll();
}
