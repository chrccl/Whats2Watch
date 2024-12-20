package com.whats2watch.w2w.controllers;

import com.whats2watch.w2w.config.Config;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceType;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import java.io.IOException;


public class RoomController {

    private static final String API_KEY = Config.loadPropertyByName("tmdb.api.key");
    private static final String TRENDING_URL = "https://api.themoviedb.org/3/trending/movie/week";

    private RoomController() {
        throw new UnsupportedOperationException("RoomController is a utility class and cannot be instantiated.");
    }

    public static Set<Room> fetchRecentRooms(User user) throws DAOException {
        return PersistanceFactory
                .createDAO(PersistanceType.DEMO)
                .createRoomDAO()
                .findAll()
                .stream()
                .map(room -> (Room) room)
                .filter(room -> room.getRoomMembers()
                        .stream()
                        .anyMatch(member -> member.getUser().equals(user)))
                .sorted(Comparator.comparing(Room::getCreationDate).reversed()) //from the newest to the oldest
                .collect(Collectors.toCollection(LinkedHashSet::new)); //to preserve order
    }

    public static Set<Genre> fetchGenres(){
        return new HashSet<>(List.of(Genre.values()));
    }

    public static Set<Media> fetchTrendingMedias() throws IOException, InterruptedException {
        Set<Media> trendingMedias = new HashSet<>();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TRENDING_URL))
                .GET()
                .setHeader("accept", "application/json")
                .setHeader("Authorization", "Bearer " + API_KEY)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonResponse = new JSONObject(response.body());
        JSONArray results = jsonResponse.getJSONArray("results");
        for (int i = 0; i < results.length(); i++)
            trendingMedias.add(mapToTrendingMediaFromJson(results.getJSONObject(i)));

        return trendingMedias;
    }

    private static Media mapToTrendingMediaFromJson(JSONObject movieJson) {
        return MediaFactory.createMovieInstance()
                .mediaId(new MediaId(
                        movieJson.getString("title"),
                        LocalDate.parse(movieJson.getString("release_date")).getYear()))
                .plot(movieJson.optString("overview", ""))
                .posterUrl(movieJson.optString("poster_path", ""))
                .build();
    }

}
