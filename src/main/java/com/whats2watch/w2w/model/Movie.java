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
                "title='" + mediaId.getTitle() + '\'' +
                ", plot='" + plot + '\'' +
                ", year=" + mediaId.getYear() +
                ", genres=" + genres +
                ", characters=" + characters.toString() +
                ", director='" + director + '\'' +
                ", productionCompanies=" + productionCompanies.toString() +
                ", watchProviders=" + watchProviders.toString() +
                ", voteAverage=" + voteAverage +
                ", Trailer='" + videoUrl + '\'' +
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
            return new Movie(this);
        }
    }
}
