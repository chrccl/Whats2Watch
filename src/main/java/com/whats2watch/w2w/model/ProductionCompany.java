package com.whats2watch.w2w.model;

import java.util.Objects;

public class ProductionCompany {

    private String companyName;

    private String logoUrl;

    public ProductionCompany(String companyName, String logoUrl) {
        this.companyName = companyName;
        this.logoUrl = logoUrl;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof ProductionCompany)) return false;

        ProductionCompany that = (ProductionCompany) o;
        return Objects.equals(getCompanyName(), that.getCompanyName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCompanyName());
    }

    @Override
    public String toString() {
        return "ProductionCompany{" +
                "companyName='" + companyName + '\'' +
                '}';
    }
}
