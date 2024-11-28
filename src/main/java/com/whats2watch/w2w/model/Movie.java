package com.whats2watch.w2w.model;

import java.util.Objects;

public class Movie extends Media{

    private String director;

    private Movie(MediaBuilder builder) {
        super(builder);
        this.director = builder.director;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Movie)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(getDirector());
        return result;
    }

    public static class MediaBuilder extends Media.MediaBuilder<MediaBuilder> {
        private String director;

        @Override
        protected MediaBuilder self() {
            return this;
        }

        @Override
        public Movie build() {
            validate();
            return new Movie(this);
        }
    }
}
