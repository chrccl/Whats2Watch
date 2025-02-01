package com.whats2watch.w2w.controllers;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.exceptions.EntityCannotBePersistedException;
import com.whats2watch.w2w.exceptions.EntityNotFoundException;
import com.whats2watch.w2w.model.User;
import com.whats2watch.w2w.model.dao.dao_factories.PersistenceFactory;
import com.whats2watch.w2w.model.dto.beans.UserBean;

public class RegisterController {

    private RegisterController() {
        throw new UnsupportedOperationException("RegisterController is a utility class and cannot be instantiated.");
    }

    public static User login(UserBean userBean) throws EntityNotFoundException {
        try{
            User user = (User) PersistenceFactory
                    .createDAO(WhatsToWatch.getPersistenceType())
                    .createUserDAO()
                    .findById(userBean.getEmail());
            return user != null && user.getPassword().equals(userBean.getPassword()) ? user : null;
        }catch (DAOException e){
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    public static void register(UserBean userBean) throws EntityCannotBePersistedException {
        try{
            User user = new User(userBean.getName(), userBean.getSurname(), userBean.getGender(), userBean.getEmail(), userBean.getPassword());
            PersistenceFactory
                    .createDAO(WhatsToWatch.getPersistenceType())
                    .createUserDAO()
                    .save(user);
        }catch (DAOException e){
            throw new EntityCannotBePersistedException(e.getMessage());
        }
    }
}
