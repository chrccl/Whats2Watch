package com.whats2watch.w2w.controllers;

import com.whats2watch.w2w.WhatsToWatch;
import com.whats2watch.w2w.config.Config;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.*;
import com.whats2watch.w2w.model.dao.dao_factories.PersistenceFactory;
import com.whats2watch.w2w.model.dao.entities.DAO;
import com.whats2watch.w2w.model.dao.entities.room.DAODatabaseRoom;
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
    
    public static Room saveRoom(User organizer, RoomBean roomBean) throws DAOException {
        Room room = RoomFactory.createRoomInstance()
                .code(IntStream.range(0, 6)
                        .mapToObj(i -> "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(random.nextInt(36)))
                        .map(Object::toString)
                        .collect(Collectors.joining()))
                .name(roomBean.getName())
                .creationDate(LocalDate.now())
                .mediaType(roomBean.getMediaType())
                .decade(roomBean.getDecade())
                .allowedGenres(roomBean.getAllowedGenres())
                .allowedProviders(roomBean.getAllowedProviders())
                .allowedProductionCompanies(roomBean.getAllowedProductionCompanies())
                .roomMembers(Set.of(new RoomMember(organizer)))
                .build();
        PersistenceFactory.createDAO(WhatsToWatch.getPersistenceType()).createRoomDAO().save(room);
        return room;
    }

    public static void updateRoomPreferences(Room room, RoomMember roomMember) throws DAOException {
        DAO<Room, String> roomDAO = PersistenceFactory.createDAO(WhatsToWatch.getPersistenceType()).createRoomDAO();
        if(roomDAO instanceof DAODatabaseRoom) {
            ((DAODatabaseRoom) roomDAO).updateLikedMedia(room.getCode(), roomMember.getUser(), roomMember.getLikedMedia());
            ((DAODatabaseRoom) roomDAO).updatePassedMedia(room.getCode(), roomMember.getUser(), roomMember.getPassedMedia());
        }else{
            roomDAO.save(room);
        }
    }

    public static List<Media> getRoomMatches(Room room) {
        return room.getRoomMembers().stream()
                .map(RoomMember::getLikedMedia)
                .reduce((set1, set2) -> {
                    set1.retainAll(set2);
                    return set1;
                })
                .map(set -> set.stream()
                        .sorted(Comparator.comparing(Media::getPopularity).reversed())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    public static Room addMemberToAnExistingRoom(User user, String roomCode) throws DAOException {
        Room room = (Room) PersistenceFactory.createDAO(WhatsToWatch.getPersistenceType()).createRoomDAO().findById(roomCode);
        if(room != null) {
            room.getRoomMembers().add(new RoomMember(user));
            PersistenceFactory.createDAO(WhatsToWatch.getPersistenceType()).createRoomDAO().save(room);
        }
        return room;
    }

    public static Set<Room> fetchRecentRooms(User user) throws DAOException {
        return PersistenceFactory
                .createDAO(WhatsToWatch.getPersistenceType())
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
        return PersistenceFactory
                .createDAO(WhatsToWatch.getPersistenceType())
                .createWatchProviderDAO()
                .findAll().stream()
                .map(watchProvider -> (WatchProvider)watchProvider)
                .collect(Collectors.toSet());
    }

    public static Set<ProductionCompany> fetchProductionCompanies() throws DAOException {
        return PersistenceFactory
                .createDAO(WhatsToWatch.getPersistenceType())
                .createProductionCompaniesDAO()
                .findAll().stream()
                .map(productionCompany -> (ProductionCompany)productionCompany)
                .collect(Collectors.toSet());
    }

    public static WatchProvider getWatchProviderByName(String providerName) throws DAOException {
        return PersistenceFactory
                .createDAO(WhatsToWatch.getPersistenceType())
                .createWatchProviderDAO()
                .findAll().stream()
                .map(watchProvider -> (WatchProvider)watchProvider)
                .filter(watchProvider -> watchProvider.getProviderName().equals(providerName))
                .findFirst().orElse(null);
    }

    public static ProductionCompany getProductionCompanyByName(String companyName) throws DAOException {
        return PersistenceFactory
                .createDAO(WhatsToWatch.getPersistenceType())
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
