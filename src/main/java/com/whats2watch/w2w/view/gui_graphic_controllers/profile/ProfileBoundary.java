package com.whats2watch.w2w.view.gui_graphic_controllers.profile;

import com.whats2watch.w2w.model.Dispatcher;
import com.whats2watch.w2w.model.User;

public class ProfileBoundary {

    private Dispatcher app;

    private User activeUser;

    public void setMainApp(Dispatcher app, User user) {
        this.app = app;
        this.activeUser = user;
    }
}
