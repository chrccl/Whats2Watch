package com.whats2watch.w2w.model;

import java.util.Set;

public class TVSerie extends Media{

    private Integer numberOfSeasons;

    public TVSerie(String title, String plot, String posterUrl, String videoUrl, Double popularity, Double voteAverage,
                   Set<Character> characters, Integer year, Set<String> genres,
                   Set<ProductionCompany> productionCompanies, Set<WatchProvider> watchProviders,
                   Integer numberOfSeasons) {
        super(title, plot, posterUrl, videoUrl, popularity, voteAverage, year, characters,
                genres, productionCompanies, watchProviders);
        this.numberOfSeasons = numberOfSeasons;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

}
