package com.whats2watch.w2w.boundaries;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.model.User;

public class HomePageBoundary {

    private WhatsToWatch app;

    public void setMainApp(WhatsToWatch app, User user) {
        this.app = app;
        //TODO fetch information to popolate homepage sections
    }
}
