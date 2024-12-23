package com.whats2watch.w2w.boundaries;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.model.User;

public class ProfileBoundary {

    private WhatsToWatch app;

    private User activeUser;

    public void setMainApp(WhatsToWatch app, User user) {
        this.app = app;
        this.activeUser = user;
    }
}
