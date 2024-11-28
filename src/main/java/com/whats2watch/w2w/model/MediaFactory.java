package com.whats2watch.w2w.model;

public class MediaFactory {

    private MediaFactory() {
        throw new UnsupportedOperationException("MediaFactory is a utility class and cannot be instantiated.");
    }

    public static <T extends Media> Movie.MediaBuilder createMovieInstance() {
        return new Movie.MediaBuilder();
    }

    public static <T extends Media> TVSeries.MediaBuilder createTVSeriesInstance() {
        return new TVSeries.MediaBuilder();
    }

}
