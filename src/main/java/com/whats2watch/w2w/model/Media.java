package com.whats2watch.w2w.model;

import java.util.Objects;
import java.util.Set;

public abstract class Media {

    protected String title;

    protected String plot;

    protected String posterUrl;

    protected String videoUrl;

    protected Double popularity;

    protected Double voteAverage;

    protected Integer year;

    protected Set<Character> characters;

    protected Set<Genre> genres;

    protected Set<ProductionCompany> productionCompanies;

    protected Set<WatchProvider> watchProviders;

    protected Media(MediaBuilder<?> builder) {
        this.title = builder.title;
        this.plot = builder.plot;
        this.posterUrl = builder.posterUrl;
        this.videoUrl = builder.videoUrl;
        this.popularity = builder.popularity;
        this.voteAverage = builder.voteAverage;
        this.year = builder.year;
        this.characters = builder.characters;
        this.genres = builder.genres;
        this.productionCompanies = builder.productionCompanies;
        this.watchProviders = builder.watchProviders;
    }

    protected Media() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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
        return Objects.equals(title, media.title) && Objects.equals(year, media.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, year);
    }

    public abstract static class MediaBuilder<T extends MediaBuilder<T>>{
        protected String title;
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

        public T title(String title) {
            this.title = title;
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

        public T year(Integer year) {
            this.year = year;
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

        protected void validate() {
            if (title == null || year == null) {
                throw new IllegalStateException("Title and year are required fields.");
            }
        }
    }
}
