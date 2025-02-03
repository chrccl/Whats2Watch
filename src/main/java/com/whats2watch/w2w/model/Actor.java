package com.whats2watch.w2w.model;

import java.util.Objects;

public class Actor {

    private String fullName;

    private Double popularity;

    private Gender gender;

    public Actor(String fullName, Double popularity, Gender gender) {
        this.fullName = fullName;
        this.popularity = popularity;
        this.gender = gender;
    }

    public String getFullName() {
        return fullName;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Actor)) return false;

        Actor actor = (Actor) o;
        return Objects.equals(getFullName(), actor.getFullName()) &&
                Objects.equals(getPopularity(), actor.getPopularity());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getFullName());
        result = 31 * result + Objects.hashCode(getPopularity());
        return result;
    }
}
