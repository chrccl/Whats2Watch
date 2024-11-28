package com.whats2watch.w2w.model;

public class MediaFactory {

    private MediaFactory() {
        throw new UnsupportedOperationException("MediaFactory is a utility class and cannot be instantiated.");
    }

    public static Movie.MediaBuilder createMovieInstance() {
        return new Movie.MediaBuilder();
    }

    public static TVSeries.MediaBuilder createTVSeriesInstance() {
        return new TVSeries.MediaBuilder();
    }

}
