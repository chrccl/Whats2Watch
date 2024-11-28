package com.whats2watch.w2w.model;

public class MediaFactory {

    public static <T extends Media> Media.Builder createInstance(Class<T> mediaClass) {
        if (mediaClass == Movie.class) {
            return new Movie.Builder();
        } else if (mediaClass == TVSeries.class) {
            return new TVSeries.Builder();
        } else {
            throw new IllegalArgumentException("Unsupported media type: " + mediaClass.getSimpleName());
        }
    }
}
