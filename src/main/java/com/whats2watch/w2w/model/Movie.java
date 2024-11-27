package com.whats2watch.w2w.model;

import java.util.Set;

public class Movie extends Media{

    private String director;

    public Movie(String title, String plot, String posterUrl, String videoUrl, Double popularity, Double voteAverage,
                 Set<Character> characters, Integer year, Set<String> genres,
                 Set<ProductionCompany> productionCompanies, Set<WatchProvider> watchProviders, String director) {
        super(title, plot, posterUrl, videoUrl, popularity, voteAverage, year, characters,
                genres, productionCompanies, watchProviders);
        this.director = director;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

}
