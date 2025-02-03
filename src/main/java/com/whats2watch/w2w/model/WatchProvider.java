package com.whats2watch.w2w.model;

import java.util.Objects;

public class WatchProvider {

    private String providerName;

    private String logoUrl;

    public WatchProvider(String providerName, String logoUrl) {
        this.providerName = providerName;
        this.logoUrl = logoUrl;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof WatchProvider)) return false;

        WatchProvider that = (WatchProvider) o;
        return Objects.equals(getProviderName(), that.getProviderName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getProviderName());
    }

    @Override
    public String toString() {
        return "WatchProvider{" +
                "providerName='" + providerName + '\'' +
                '}';
    }
}
