package com.whats2watch.w2w.model;

import java.util.Objects;
import java.util.Set;

public abstract class Media {

    protected MediaId mediaId;

    protected String plot;

    protected String posterUrl;

    protected String videoUrl;

    protected Double popularity;

    protected Double voteAverage;

    protected Set<Character> characters;

    protected Set<Genre> genres;

    protected Set<ProductionCompany> productionCompanies;

    protected Set<WatchProvider> watchProviders;

    protected Media(MediaBuilder<?> builder) {
        this.mediaId = builder.mediaId;
        this.plot = builder.plot;
        this.posterUrl = builder.posterUrl;
        this.videoUrl = builder.videoUrl;
        this.popularity = builder.popularity;
        this.voteAverage = builder.voteAverage;
        this.characters = builder.characters;
        this.genres = builder.genres;
        this.productionCompanies = builder.productionCompanies;
        this.watchProviders = builder.watchProviders;
    }

    public MediaId getMediaId() {
        return mediaId;
    }

    public void setMediaId(MediaId mediaId) {
        this.mediaId = mediaId;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Set<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(Set<Character> characters) {
        this.characters = characters;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Set<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(Set<ProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public Set<WatchProvider> getWatchProviders() {
        return watchProviders;
    }

    public void setWatchProviders(Set<WatchProvider> watchProviders) {
        this.watchProviders = watchProviders;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Media)) return false;

        Media media = (Media) o;
        return Objects.equals(getMediaId(), media.getMediaId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getMediaId());
    }

    public abstract static class MediaBuilder<T extends MediaBuilder<T>>{
        protected MediaId mediaId;
        protected String plot;
        protected String posterUrl;
        protected String videoUrl;
        protected Double popularity;
        protected Double voteAverage;
        protected Integer year;
        protected Set<Character> characters = Set.of();
        protected Set<Genre> genres = Set.of();
        protected Set<ProductionCompany> productionCompanies = Set.of();
        protected Set<WatchProvider> watchProviders = Set.of();

        public T mediaId(MediaId mediaId) {
            this.mediaId = mediaId;
            return self();
        }

        public T plot(String plot) {
            this.plot = plot;
            return self();
        }

        public T posterUrl(String posterUrl) {
            this.posterUrl = posterUrl;
            return self();
        }

        public T videoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            return self();
        }

        public T popularity(Double popularity) {
            this.popularity = popularity;
            return self();
        }

        public T voteAverage(Double voteAverage) {
            this.voteAverage = voteAverage;
            return self();
        }

        public T characters(Set<Character> characters) {
            this.characters = characters;
            return self();
        }

        public T genres(Set<Genre> genres) {
            this.genres = genres;
            return self();
        }

        public T productionCompanies(Set<ProductionCompany> productionCompanies) {
            this.productionCompanies = productionCompanies;
            return self();
        }

        public T watchProviders(Set<WatchProvider> watchProviders) {
            this.watchProviders = watchProviders;
            return self();
        }

        protected abstract T self();

        public abstract Media build();

    }
}
