package com.whats2watch.w2w.model;

import com.whats2watch.w2w.annotations.PersistenceConstructor;

import java.util.Objects;

public class Movie extends Media{

    private String director;

    @PersistenceConstructor
    Movie() { } //Package-Private Constructor

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

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", plot='" + plot + '\'' +
                ", year=" + year +
                ", genres=" + genres +
                ", characters=" + characters +
                ", director='" + director + '\'' +
                ", productionCompanies=" + productionCompanies +
                ", watchProviders=" + watchProviders +
                ", popularity=" + popularity +
                ", voteAverage=" + voteAverage +
                ", posterUrl='" + posterUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }

    public static class MediaBuilder extends Media.MediaBuilder<MediaBuilder> {
        private String director;

        public MediaBuilder director(String director) {
            this.director = director;
            return this;
        }

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
