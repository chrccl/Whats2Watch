package com.whats2watch.w2w.model;

import java.util.Objects;

public class TVSeries extends Media{

    private Integer numberOfSeasons;

    private Integer numberOfEpisodes;

    private TVSeries(MediaBuilder builder) {
        super(builder);
        this.numberOfSeasons = builder.numberOfSeasons;
        this.numberOfEpisodes = builder.numberOfEpisodes;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(Integer numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof TVSeries)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(getNumberOfSeasons());
        return result;
    }

    @Override
    public String toString() {
        return "TVSeries{" +
                "title='" + mediaId.getTitle() + '\'' +
                ", plot='" + plot + '\'' +
                ", year=" + mediaId.getYear() +
                ", genres=" + genres +
                ", numberOfSeasons=" + numberOfSeasons +
                ", numberOfEpisodes=" + numberOfEpisodes +
                ", watchProviders=" + watchProviders +
                ", characters=" + characters +
                ", productionCompanies=" + productionCompanies +
                ", popularity=" + popularity +
                ", voteAverage=" + voteAverage +
                ", posterUrl='" + posterUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }

    public static class MediaBuilder extends Media.MediaBuilder<MediaBuilder> {
        private Integer numberOfSeasons;
        private Integer numberOfEpisodes;

        public MediaBuilder seasons(Integer numberOfSeasons, Integer numberOfEpisodes) {
            this.numberOfSeasons = numberOfSeasons;
            this.numberOfEpisodes = numberOfEpisodes;
            return this;
        }

        public MediaBuilder numberOfSeasons(Integer numberOfSeasons) {
            this.numberOfSeasons = numberOfSeasons;
            return this;
        }

        public MediaBuilder numberOfEpisodes(Integer numberOfEpisodes) {
            this.numberOfEpisodes = numberOfEpisodes;
            return this;
        }

        @Override
        protected MediaBuilder self() {
            return this;
        }

        @Override
        public TVSeries build() {
            return new TVSeries(this);
        }
    }
}
