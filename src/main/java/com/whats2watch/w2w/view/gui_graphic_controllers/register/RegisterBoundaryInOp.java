package com.whats2watch.w2w.view.gui_graphic_controllers.register;

import com.whats2watch.w2w.exceptions.DAOException;

public interface RegisterBoundaryInOp {

    void handleLogin() throws DAOException;

    void handleRegister() throws DAOException;

    void handleGoToRegisterPageEvent();

    void handleGoToLoginPageEvent();

}
