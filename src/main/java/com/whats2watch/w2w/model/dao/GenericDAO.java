package com.whats2watch.w2w.model.dao;

import java.util.Map;

public interface GenericDAO<T> {
    Boolean save(T entity);
    T findById(Map<String, Object> compositeKey);
    Boolean delete(Map<String, Object> compositeKey);
}
