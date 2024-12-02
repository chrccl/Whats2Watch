package com.whats2watch.w2w.model;

import com.whats2watch.w2w.config.Config;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public abstract class MediaTMDBFetcher<T extends Media> {
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/";
    private static final String RESULTS = "results";
    private static final String API_KEY = Config.loadPropertyByName("tmdb.api.key");
    private final HttpClient httpClient = HttpClient.newHttpClient();

    protected List<T> fetchTopMedia(int year, String mediaType) throws URISyntaxException, IOException, InterruptedException {
        List<T> mediaList = new ArrayList<>();
        for (int page = 1; page <= 10; page++) {  // Adjust as needed (20 results per page).
            String url = String.format("%s/discover/%s?sort_by=popularity.desc&year=%d&page=%d",
                    BASE_URL, mediaType, year, page);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .setHeader("accept", "application/json")
                    .setHeader("Authorization", "Bearer " + API_KEY)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray results = new JSONObject(response.body()).getJSONArray(RESULTS);

            for (int i = 0; i < results.length(); i++) {
                int mediaId = results.getJSONObject(i).getInt("id");
                mediaList.add(fetchMediaDetails(mediaId, year, mediaType));
            }
        }
        return mediaList;
    }

    private T fetchMediaDetails(int mediaId, int year, String mediaType) throws URISyntaxException, IOException, InterruptedException {
        String url = String.format("%s/%s/%d?append_to_response=credits,videos,watch/providers",
                BASE_URL, mediaType, mediaId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .setHeader("accept", "application/json")
                .setHeader("Authorization", "Bearer " + API_KEY)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());

        return buildMedia(json, year);
    }


    protected Set<Genre> parseGenres(JSONArray genresJson) {
        Set<Genre> genres = new HashSet<>();
        for (int i = 0; i < genresJson.length(); i++) {
            genres.add(Genre.of(genresJson.getJSONObject(i).getString("name")));
        }
        return genres;
    }

    protected Set<ProductionCompany> parseProductionCompanies(JSONArray companiesJson) {
        Set<ProductionCompany> companies = new HashSet<>();
        for (int i = 0; i < companiesJson.length(); i++) {
            JSONObject company = companiesJson.getJSONObject(i);
            String logoPath = company.optString("logo_path", "");
            String logoUrl = logoPath.isEmpty() ? "" : IMAGE_BASE_URL + logoPath;
            companies.add(new ProductionCompany(company.getString("name"), logoUrl));
        }
        return companies;
    }

    protected Set<Character> parseCharacters(JSONArray castJson) {
        TreeSet<Character> characterSet = new TreeSet<>((c1, c2) -> {
            int comparison = Double.compare(c2.getActor().getPopularity(), c1.getActor().getPopularity());
            return comparison != 0 ? comparison : c1.getActor().getFullName().compareTo(c2.getActor().getFullName());
        });

        for (int i = 0; i < castJson.length(); i++) {
            JSONObject castMember = castJson.getJSONObject(i);
            String characterName = castMember.optString("character", "Unknown");
            String actorName = castMember.optString("name", "Unknown");
            double popularity = castMember.optDouble("popularity", 0.0);
            int genderIndex = castMember.optInt("gender", 0);
            Gender gender = (genderIndex == 1) ? Gender.FEMALE : Gender.MALE;

            Actor actor = new Actor(actorName, popularity, gender);
            characterSet.add(new Character(characterName, actor));
        }

        return characterSet.stream().limit(10).collect(Collectors.toSet());
    }

    protected Set<WatchProvider> parseWatchProviders(JSONObject json) {
        Set<WatchProvider> providers = new HashSet<>();
        JSONObject watchProviders = getWatchProvidersForRegion(json);
        if (watchProviders != null) {
            JSONArray streamingOptions = watchProviders.optJSONArray("flatrate");
            if (streamingOptions != null) {
                extractStreamingProviders(streamingOptions, providers);
            }
        }
        return providers;
    }

    protected String parseTrailerUrl(JSONObject json) {
        JSONArray videos = json.optJSONObject("videos").optJSONArray(RESULTS);
        if (videos != null) {
            for (int i = 0; i < videos.length(); i++) {
                JSONObject video = videos.getJSONObject(i);
                if ("Trailer".equalsIgnoreCase(video.optString("type"))) {
                    return "https://www.youtube.com/watch?v=" + video.optString("key");
                }
            }
        }
        return "";
    }

    private JSONObject getWatchProvidersForRegion(JSONObject json) {
        JSONObject watchProviders = json.optJSONObject("watch/providers").optJSONObject(RESULTS);
        return (watchProviders != null && watchProviders.has("US")) ? watchProviders.getJSONObject("US") : null;
    }

    private void extractStreamingProviders(JSONArray streamingOptions, Set<WatchProvider> providers) {
        Set<String> popularProviders = Set.of("netflix", "disney", "prime", "hulu", "hbo", "apple tv");
        for (int i = 0; i < streamingOptions.length(); i++) {
            JSONObject provider = streamingOptions.getJSONObject(i);
            String providerName = provider.getString("provider_name").toLowerCase();
            if (popularProviders.stream().anyMatch(providerName::contains)) {
                String logoUrl = buildLogoUrl(provider.optString("logo_path", ""));
                providers.add(new WatchProvider(provider.getString("provider_name"), logoUrl));
            }
        }
    }

    private String buildLogoUrl(String logoPath) {
        return logoPath.isEmpty() ? "" : IMAGE_BASE_URL + logoPath;
    }

    protected abstract T buildMedia(JSONObject json, int year);
}
