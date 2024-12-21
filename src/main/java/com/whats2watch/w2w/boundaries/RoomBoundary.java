package com.whats2watch.w2w.boundaries;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.model.Genre;

public class RoomBoundary {

    private WhatsToWatch app;

    public void setMainApp(WhatsToWatch app, Genre genre) {
        this.app = app;
    }
}
