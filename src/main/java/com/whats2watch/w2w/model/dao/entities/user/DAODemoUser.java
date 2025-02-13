package com.whats2watch.w2w.model.dao.entities.user;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dao.entities.DAO;

import java.util.HashSet;
import java.util.Set;

public class DAODemoUser implements DAO<User, String> {

    private static final Set<User> users = new HashSet<>();
    private static DAODemoUser instance;

    private DAODemoUser() {}

    public static synchronized DAODemoUser getInstance() {
        if (instance == null) {
            instance = new DAODemoUser();
        }
        return instance;
    }

    @Override
    public void save(User entity) throws DAOException {
        users.add(entity);
    }

    @Override
    public User findById(String entityKey) throws DAOException {
        return users.stream()
                .filter(user -> user.getEmail().equals(entityKey))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteById(String entityKey) throws DAOException {
        User user = findById(entityKey);
        if (user != null)
            users.remove(findById(entityKey));
    }

    @Override
    public Set<User> findAll() throws DAOException  {
        return users;
    }
}
