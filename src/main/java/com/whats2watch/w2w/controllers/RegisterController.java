package com.whats2watch.w2w.controllers;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceType;

public class RegisterController {

    private RegisterController() {
        throw new UnsupportedOperationException("RegisterController is a utility class and cannot be instantiated.");
    }

    public static User login(String email, String password) throws DAOException {
        User user = (User) PersistanceFactory
                .createDAO(PersistanceType.DEMO)
                .createUserDAO()
                .findById(email);
        return user != null && user.getPassword().equals(password) ? user : null;
    }
}
