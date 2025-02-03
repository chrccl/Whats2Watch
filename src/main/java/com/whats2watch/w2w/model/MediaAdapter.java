package com.whats2watch.w2w.model;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MediaAdapter implements JsonDeserializer<Media> {

    @Override
    public Media deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String director = jsonObject.get("director").getAsString();

        if (director != null) {
            return context.deserialize(json, Movie.class);
        } else {
            return context.deserialize(json, TVSeries.class);
        }

    }
}