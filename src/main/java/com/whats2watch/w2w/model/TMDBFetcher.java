package com.whats2watch.w2w.model;

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

public class TMDBFetcher {
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1ZjY4MTU5NjlmOGNkZjllMWNiYzRiZDU1YTk5MGRlYSIsIm5iZiI6MTczMDEzMzU0OS4zMjIzNTcsInN1YiI6IjY3MTY2YTlmNGJmMzdjODgzY2I3MjBlYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.AyXTT2E_LYX2z72RkgPX7jzIWOQgs7k-6J-17J86iyc";
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    private final HttpClient httpClient;

    public TMDBFetcher() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<Movie> fetchTopMovies(int year) throws URISyntaxException, IOException, InterruptedException {
        List<Movie> movies = new ArrayList<>();

        for (int page = 1; page <= 25; page++) {  // 20 movies per page, 25 pages for 500 movies
            String url = String.format("%s/discover/movie?api_key=%s&sort_by=popularity.desc&primary_release_year=%d&page=%d",
                    BASE_URL, API_KEY, year, page);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .setHeader("accept", "application/json")
                    .setHeader("Authorization", "Bearer " + API_KEY)
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray results = new JSONObject(response.body()).getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                int movieId = results.getJSONObject(i).getInt("id");
                Movie movie = fetchMovieDetails(movieId, year);
                System.out.println(movie);
                movies.add(movie);
            }
        }

        return movies;
    }

    private Movie fetchMovieDetails(int movieId, int year) throws URISyntaxException, IOException, InterruptedException {
        String url = String.format("%s/movie/%d?api_key=%s&append_to_response=credits,watch/providers,videos",
                BASE_URL, movieId, API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .setHeader("accept", "application/json")
                .setHeader("Authorization", "Bearer " + API_KEY)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject movieJson = new JSONObject(response.body());

        return buildMovie(movieJson, year);
    }

    private Movie buildMovie(JSONObject json, int year) {
        JSONObject credits = json.getJSONObject("credits");
        return MediaFactory.createMovieInstance()
                .title(json.getString("title"))
                .plot(json.optString("overview", ""))
                .posterUrl(json.optString("poster_path", ""))
                .videoUrl(parseTrailerUrl(json))
                .popularity(json.optDouble("popularity"))
                .voteAverage(json.optDouble("vote_average"))
                .year(year)
                .genres(parseGenres(json.getJSONArray("genres")))
                .productionCompanies(parseProductionCompanies(json.getJSONArray("production_companies")))
                .watchProviders(parseWatchProviders(json))
                .characters(parseCharacters(credits.getJSONArray("cast")))
                .director(parseDirector(credits.getJSONArray("crew")))
                .build();
    }

    private String parseTrailerUrl(JSONObject json) {
        JSONArray videos = json.optJSONObject("videos").optJSONArray("results");
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


    private Set<String> parseGenres(JSONArray genresJson) {
        Set<String> genres = new HashSet<>();
        for (int i = 0; i < genresJson.length(); i++) {
            genres.add(genresJson.getJSONObject(i).getString("name"));
        }
        return genres;
    }

    private Set<ProductionCompany> parseProductionCompanies(JSONArray companiesJson) {
        Set<ProductionCompany> companies = new HashSet<>();
        String baseUrl = "https://image.tmdb.org/t/p/w500/";

        for (int i = 0; i < companiesJson.length(); i++) {
            JSONObject company = companiesJson.getJSONObject(i);
            String logoPath = company.optString("logo_path", "");
            String logoUrl = logoPath.isEmpty() ? "" : baseUrl + logoPath;
            companies.add(new ProductionCompany(company.getString("name"), logoUrl));
        }

        return companies;
    }

    private Set<WatchProvider> parseWatchProviders(JSONObject json) {
        Set<WatchProvider> providers = new HashSet<>();
        String baseUrl = "https://image.tmdb.org/t/p/w500/";
        JSONObject watchProviders = json.optJSONObject("watch/providers").optJSONObject("results");
        if (watchProviders != null && watchProviders.has("US")) { // Example: focus on US providers
            JSONArray streamingOptions = watchProviders.getJSONObject("US").optJSONArray("flatrate");
            if (streamingOptions != null) {
                for (int i = 0; i < streamingOptions.length(); i++) {
                    JSONObject provider = streamingOptions.getJSONObject(i);
                    String providerName = provider.getString("provider_name").toLowerCase();

                    // Filter only popular streaming providers
                    if (providerName.contains("netflix") || providerName.contains("disney") ||
                            providerName.contains("prime") || providerName.contains("hulu") ||
                            providerName.contains("hbo") || providerName.contains("apple tv")) {
                        String logoPath = provider.optString("logo_path", "");
                        String logoUrl = logoPath.isEmpty() ? "" : baseUrl + logoPath;
                        providers.add(new WatchProvider(provider.getString("provider_name"), logoUrl));
                    }
                }
            }
        }
        return providers;
    }


    private String parseDirector(JSONArray crewJson) {
        for (int i = 0; i < crewJson.length(); i++) {
            JSONObject crewMember = crewJson.getJSONObject(i);
            if ("Director".equalsIgnoreCase(crewMember.getString("job"))) {
                return crewMember.getString("name");
            }
        }
        return "Unknown";
    }

    private Set<Character> parseCharacters(JSONArray castJson) {
        TreeSet<Character> characterSet = new TreeSet<>((c1, c2) -> {
            int comparison = Double.compare(c2.getActor().getPopularity(), c1.getActor().getPopularity());
            if (comparison == 0) {
                return c1.getActor().getFullName().compareTo(c2.getActor().getFullName());
            }
            return comparison;
        });
        for (int i = 0; i < castJson.length(); i++) {
            JSONObject castMember = castJson.getJSONObject(i);
            String characterName = castMember.optString("character", "Unknown");
            String actorName = castMember.optString("name", "Unknown");
            double popularity = castMember.optDouble("popularity", 0.0);
            int genderIndex = castMember.optInt("gender", 0);
            Gender gender = (genderIndex > 2 || genderIndex < 0) ? Gender.UNKNOWN : Gender.values()[genderIndex];

            Actor actor = new Actor(actorName, popularity, gender);
            characterSet.add(new Character(characterName, actor));
        }

        // Return the top 10 characters
        return characterSet.stream().limit(10).collect(Collectors.toSet());
    }

}
