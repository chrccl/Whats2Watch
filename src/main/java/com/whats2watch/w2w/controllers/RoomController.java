package com.whats2watch.w2w.controllers;

import com.whats2watch.w2w.config.Config;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceFactory;
import com.whats2watch.w2w.model.dao.dao_factories.PersistanceType;
import com.whats2watch.w2w.model.dto.beans.RoomBean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.io.IOException;


public class RoomController {

    private static final String API_KEY = Config.loadPropertyByName("tmdb.api.key");

    private static final String TRENDING_URL = "https://api.themoviedb.org/3/trending/movie/week";

    private static final SecureRandom random = new SecureRandom();

    private RoomController() {
        throw new UnsupportedOperationException("RoomController is a utility class and cannot be instantiated.");
    }
    
    public static String saveRoom(User organizer, RoomBean roomBean) throws DAOException {
        String roomCode = IntStream.range(0, 6)
                .mapToObj(i -> "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(random.nextInt(36)))
                .map(Object::toString)
                .collect(Collectors.joining());
        Room room = RoomFactory.createRoomInstance()
                .code(roomCode)
                .name(roomBean.getName())
                .creationDate(LocalDate.now())
                .mediaType(roomBean.getMediaType())
                .decade(roomBean.getDecade())
                .allowedGenres(roomBean.getAllowedGenres())
                .allowedProviders(roomBean.getAllowedProviders())
                .allowedProductionCompanies(roomBean.getAllowedProductionCompanies())
                .roomMembers(Set.of(new RoomMember(organizer)))
                .build();
        PersistanceFactory.createDAO(PersistanceType.DEMO).createRoomDAO().save(room);
        return roomCode;
    }

    public static boolean addMemberToAnExistingRoom(User user, String roomCode) throws DAOException {
        boolean result = false;
        Room room = (Room) PersistanceFactory.createDAO(PersistanceType.DEMO).createRoomDAO().findById(roomCode);
        if(room != null) {
            room.getRoomMembers().add(new RoomMember(user));
            PersistanceFactory.createDAO(PersistanceType.DEMO).createRoomDAO().save(room);
            result = true;
        }
        return result;
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

    public static Set<WatchProvider> fetchWatchProviders() throws DAOException {
        return PersistanceFactory
                .createDAO(PersistanceType.DEMO)
                .createWatchProviderDAO()
                .findAll().stream()
                .map(watchProvider -> (WatchProvider)watchProvider)
                .collect(Collectors.toSet());
    }

    public static Set<ProductionCompany> fetchProductionCompanies() throws DAOException {
        return PersistanceFactory
                .createDAO(PersistanceType.DEMO)
                .createProductionCompaniesDAO()
                .findAll().stream()
                .map(productionCompany -> (ProductionCompany)productionCompany)
                .collect(Collectors.toSet());
    }

    public static WatchProvider getWatchProviderByName(String providerName) throws DAOException {
        return PersistanceFactory
                .createDAO(PersistanceType.DEMO)
                .createWatchProviderDAO()
                .findAll().stream()
                .map(watchProvider -> (WatchProvider)watchProvider)
                .filter(watchProvider -> watchProvider.getProviderName().equals(providerName))
                .findFirst().orElse(null);
    }

    public static ProductionCompany getProductionCompanyByName(String companyName) throws DAOException {
        return PersistanceFactory
                .createDAO(PersistanceType.DEMO)
                .createProductionCompaniesDAO()
                .findAll().stream()
                .map(productionCompany -> (ProductionCompany)productionCompany)
                .filter(productionCompany -> productionCompany.getCompanyName().equals(companyName))
                .findFirst().orElse(null);
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
