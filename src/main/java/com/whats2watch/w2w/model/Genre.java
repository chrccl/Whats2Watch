package com.whats2watch.w2w.model;

public enum Genre {
    ACTION("Action"),
    ADVENTURE("Adventure"),
    ACTION_ADVENTURE("Action & Adventure"),
    ANIMATION("Animation"),
    COMEDY("Comedy"),
    CRIME("Crime"),
    DOCUMENTARY("Documentary"),
    DRAMA("Drama"),
    FAMILY("Family"),
    FANTASY("Fantasy"),
    HISTORY("History"),
    HORROR("Horror"),
    KIDS("Kids"),
    MUSIC("Music"),
    MYSTERY("Mystery"),
    NEWS("News"),
    REALITY("Reality"),
    ROMANCE("Romance"),
    SCIENCE_FICTION("Science Fiction"),
    SCI_FI_FANTASY("Sci-Fi & Fantasy"),
    SOAP("Soap"),
    TALK("Talk"),
    TV_MOVIE("TV Movie"),
    THRILLER("Thriller"),
    WAR("War"),
    WAR_POLITICS("War & Politics"),
    WESTERN("Western");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }

    public static Genre of(String name) {
        for (Genre genre : values()) {
            if (genre.displayName.equalsIgnoreCase(name)) {
                return genre;
            }
        }
        throw new IllegalArgumentException("No enum constant found for: " + name);
    }
}

