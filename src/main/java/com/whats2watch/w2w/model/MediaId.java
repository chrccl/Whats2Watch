package com.whats2watch.w2w.model;

import java.util.Objects;

public class MediaId {

    private String title;

    private Integer year;

    public MediaId(String title, Integer year) {
        this.title = title;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof MediaId)) return false;

        MediaId mediaId = (MediaId) o;
        return Objects.equals(getTitle(), mediaId.getTitle()) && Objects.equals(getYear(), mediaId.getYear());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getTitle());
        result = 31 * result + Objects.hashCode(getYear());
        return result;
    }

    @Override
    public String toString() {
        return "MediaId{" +
                "title='" + title + '\'' +
                ", year='" + year + '\'' +
                '}';
    }
}
