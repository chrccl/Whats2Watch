package com.whats2watch.w2w.model.dto.beans;

import com.whats2watch.w2w.model.*;

import java.util.Set;

public class RoomBean {

    private String name;

    private MediaType mediaType;

    private Integer decade;

    private Set<Genre> allowedGenres;

    private Set<WatchProvider> allowedProviders;

    private Set<ProductionCompany> allowedProductionCompanies;

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

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public Integer getDecade() {
        return decade;
    }

    public void setDecade(Integer decade) {
        this.decade = decade;
    }

    public Set<Genre> getAllowedGenres() {
        return allowedGenres;
    }

    public void setAllowedGenres(Set<Genre> allowedGenres) {
        this.allowedGenres = allowedGenres;
    }

    public Set<WatchProvider> getAllowedProviders() {
        return allowedProviders;
    }

    public void setAllowedProviders(Set<WatchProvider> allowedProviders) {
        this.allowedProviders = allowedProviders;
    }

    public Set<ProductionCompany> getAllowedProductionCompanies() {
        return allowedProductionCompanies;
    }

    public void setAllowedProductionCompanies(Set<ProductionCompany> allowedProductionCompanies) {
        this.allowedProductionCompanies = allowedProductionCompanies;
    }

}
