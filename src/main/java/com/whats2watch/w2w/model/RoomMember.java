package com.whats2watch.w2w.model;

import java.util.Objects;
import java.util.Set;

public class RoomMember {

    private User user;

    private Set<Media> likedMedia;

    private Set<Media> passedMedia;

    public RoomMember(User user, Set<Media> likedMedia, Set<Media> passedMedia) {
        this.user = user;
        this.likedMedia = likedMedia;
        this.passedMedia = passedMedia;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Media> getLikedMedia() {
        return likedMedia;
    }

    public void setLikedMedia(Set<Media> likedMedia) {
        this.likedMedia = likedMedia;
    }

    public Set<Media> getPassedMedia() {
        return passedMedia;
    }

    public void setPassedMedia(Set<Media> passedMedia) {
        this.passedMedia = passedMedia;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof RoomMember)) return false;

        RoomMember that = (RoomMember) o;
        return Objects.equals(getUser(), that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUser());
    }

    @Override
    public String toString() {
        return "RoomMember{" +
                "user=" + user +
                ", likedMedia=" + likedMedia +
                ", passedMedia=" + passedMedia +
                '}';
    }
}