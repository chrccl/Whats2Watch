package com.whats2watch.w2w.controllers;

import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceType;
import com.whats2watch.w2w.model.dto.beans.UserBean;

public class RegisterController {

    private RegisterController() {
        throw new UnsupportedOperationException("RegisterController is a utility class and cannot be instantiated.");
    }

    public static User login(UserBean userBean) throws DAOException {
        User user = (User) PersistanceFactory
                .createDAO(PersistanceType.FILESYSTEM)
                .createUserDAO()
                .findById(userBean.getEmail());
        return user != null && user.getPassword().equals(userBean.getPassword()) ? user : null;
    }

    public static void register(UserBean userBean) throws DAOException {
        User user = new User(userBean.getName(), userBean.getSurname(), userBean.getGender(), userBean.getEmail(), userBean.getPassword());
        PersistanceFactory
                .createDAO(PersistanceType.FILESYSTEM)
                .createUserDAO()
                .save(user);
    }
}
