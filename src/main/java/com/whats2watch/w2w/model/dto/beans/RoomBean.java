package com.whats2watch.w2w.model.dto.beans;

import com.whats2watch.w2w.model.*;

import java.util.Set;

public class RoomBean {

    private String name;

    private final MediaType mediaType;

    private final Integer decade;

    private final Set<Genre> allowedGenres;

    private final Set<WatchProvider> allowedProviders;

    private final Set<ProductionCompany> allowedProductionCompanies;

    public RoomBean(String name, MediaType mediaType, Integer decade, Set<Genre> allowedGenres,
                    Set<WatchProvider> allowedProviders, Set<ProductionCompany> allowedProductionCompanies) {
        this.name = name;
        this.mediaType = mediaType;
        this.decade = decade;
        this.allowedGenres = allowedGenres;
        this.allowedProviders = allowedProviders;
        this.allowedProductionCompanies = allowedProductionCompanies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public Integer getDecade() {
        return decade;
    }

    public Set<Genre> getAllowedGenres() {
        return allowedGenres;
    }

    public Set<WatchProvider> getAllowedProviders() {
        return allowedProviders;
    }

    public Set<ProductionCompany> getAllowedProductionCompanies() {
        return allowedProductionCompanies;
    }

}
