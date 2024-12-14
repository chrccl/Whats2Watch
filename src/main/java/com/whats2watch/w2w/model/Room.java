package com.whats2watch.w2w.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class Room {

    private String code;

    private String name;

    private LocalDate creationDate;

    private MediaType mediaType;

    private Integer decade;

    private Set<Genre> allowedGenres;

    private Set<WatchProvider> allowedProviders;

    private Set<ProductionCompany> allowedProductionCompanies;

    private Set<RoomMember> roomMembers;

    private Room(RoomBuilder builder) {
        this.code = builder.code;
        this.name = builder.name;
        this.creationDate = builder.creationDate;
        this.mediaType = builder.mediaType;
        this.decade = builder.decade;
        this.allowedGenres = builder.allowedGenres;
        this.allowedProviders = builder.allowedProviders;
        this.allowedProductionCompanies = builder.allowedProductionCompanies;
        this.roomMembers = builder.roomMembers;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
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

    public Set<RoomMember> getRoomMembers() {
        return roomMembers;
    }

    public void setRoomMembers(Set<RoomMember> roomMembers) {
        this.roomMembers = roomMembers;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Room)) return false;

        Room room = (Room) o;
        return Objects.equals(getCode(), room.getCode()) && Objects.equals(getCreationDate(), room.getCreationDate());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getCode());
        result = 31 * result + Objects.hashCode(getCreationDate());
        return result;
    }

    @Override
    public String toString() {
        return "Room{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                ", mediaType=" + mediaType +
                ", decade=" + decade +
                ", allowedGenres=" + allowedGenres +
                ", allowedProviders=" + allowedProviders +
                ", allowedProductionCompanies=" + allowedProductionCompanies +
                ", roomMembers=" + roomMembers +
                '}';
    }

    public static class RoomBuilder {
        private String code;
        private String name;
        private LocalDate creationDate;
        private MediaType mediaType;
        private Integer decade;
        private Set<Genre> allowedGenres = Set.of();
        private Set<WatchProvider> allowedProviders = Set.of();
        private Set<ProductionCompany> allowedProductionCompanies = Set.of();
        private Set<RoomMember> roomMembers = Set.of();

        public RoomBuilder code(String code) {
            this.code = code;
            return this;
        }

        public RoomBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RoomBuilder creationDate(LocalDate creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public RoomBuilder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public RoomBuilder decade(Integer decade) {
            this.decade = decade;
            return this;
        }

        public RoomBuilder allowedGenres(Set<Genre> allowedGenres) {
            this.allowedGenres = allowedGenres;
            return this;
        }

        public RoomBuilder allowedProviders(Set<WatchProvider> allowedProviders) {
            this.allowedProviders = allowedProviders;
            return this;
        }

        public RoomBuilder allowedProductionCompanies(Set<ProductionCompany> allowedProductionCompanies) {
            this.allowedProductionCompanies = allowedProductionCompanies;
            return this;
        }

        public RoomBuilder roomMembers(Set<RoomMember> roomMembers) {
            this.roomMembers = roomMembers;
            return this;
        }

        public Room build() {
            return new Room(this);
        }
    }
}
