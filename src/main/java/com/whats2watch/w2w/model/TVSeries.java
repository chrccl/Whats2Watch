package com.whats2watch.w2w.model;

import java.util.Objects;

public class TVSeries extends Media{

    private Integer numberOfSeasons;

    private TVSeries(MediaBuilder builder) {
        super(builder);
        this.numberOfSeasons = builder.numberOfSeasons;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
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

    public static class MediaBuilder extends Media.MediaBuilder<MediaBuilder> {
        private Integer numberOfSeasons;

        public MediaBuilder seasons(Integer numberOfSeasons) {
            this.numberOfSeasons = numberOfSeasons;
            return this;
        }

        @Override
        protected MediaBuilder self() {
            return this;
        }

        @Override
        public TVSeries build() {
            validate();
            return new TVSeries(this);
        }
    }
}
