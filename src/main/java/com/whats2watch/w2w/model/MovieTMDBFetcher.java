package com.whats2watch.w2w.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class MovieTMDBFetcher extends MediaTMDBFetcher<Movie> {

    public List<Movie> fetchTopMovies(int year) throws URISyntaxException, IOException, InterruptedException {
        return fetchTopMedia(year, "movie");
    }

    @Override
    protected Movie buildMedia(JSONObject json, int year) {
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

    private String parseDirector(JSONArray crewJson) {
        for (int i = 0; i < crewJson.length(); i++) {
            JSONObject crewMember = crewJson.getJSONObject(i);
            if ("Director".equalsIgnoreCase(crewMember.getString("job"))) {
                return crewMember.getString("name");
            }
        }
        return "Unknown";
    }
}
