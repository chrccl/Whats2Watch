package com.whats2watch.w2w.model.dto.beans;

import com.whats2watch.w2w.model.MediaType;

public class RoomBean {

    private String name;

    private MediaType mediaType;

    private Integer decade;

    public RoomBean(String name, MediaType mediaType, Integer decade) {
        this.name = name;
        this.mediaType = mediaType;
        this.decade = decade;
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
}
