package com.whats2watch.w2w.model;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class TVSeriesTMDBFetcher extends MediaTMDBFetcher<TVSeries> {

    public List<TVSeries> fetchTopTVSeries(int year) throws URISyntaxException, IOException, InterruptedException {
        return fetchTopMedia(year, "tv");
    }

    @Override
    protected TVSeries buildMedia(JSONObject json, int year) {
        return MediaFactory.createTVSeriesInstance()
                .title(json.getString("name"))
                .plot(json.optString("overview", ""))
                .posterUrl(json.optString("poster_path", ""))
                .videoUrl(parseTrailerUrl(json))
                .popularity(json.optDouble("popularity"))
                .voteAverage(json.optDouble("vote_average"))
                .year(year)
                .numberOfSeasons(json.getInt("number_of_seasons"))
                .numberOfEpisodes(json.getInt("number_of_episodes"))
                .genres(parseGenres(json.getJSONArray("genres")))
                .productionCompanies(parseProductionCompanies(json.getJSONArray("production_companies")))
                .watchProviders(parseWatchProviders(json))
                .characters(parseCharacters(json.getJSONObject("credits").getJSONArray("cast")))
                .build();
    }
}
