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

    protected Set<String> genres;

    protected Set<ProductionCompany> productionCompanies;

    protected Set<WatchProvider> watchProviders;

    protected Media(String title, String plot, String posterUrl, String videoUrl, Double popularity, Double voteAverage,
                 Integer year, Set<Character> characters, Set<String> genres,
                 Set<ProductionCompany> productionCompanies, Set<WatchProvider> watchProviders) {
        this.title = title;
        this.plot = plot;
        this.posterUrl = posterUrl;
        this.videoUrl = videoUrl;
        this.popularity = popularity;
        this.voteAverage = voteAverage;
        this.year = year;
        this.characters = characters;
        this.genres = genres;
        this.productionCompanies = productionCompanies;
        this.watchProviders = watchProviders;
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

    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
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
}
