package com.aqulasoft.disyam.models.audio;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class YaSearchResult {
    private final String searchStr;
    private List<YaTrack> tracks;
    private List<YaArtist> artists;
    private List<YaPlaylist> playlists;
    private int perPage;
    private int total;
    private String searchType;

    public YaSearchResult(JSONObject json, String type) {
        searchStr = json.getString("text");

        switch (type) {
            case "all":
                if (json.has("best")) {
                    JSONObject best = json.getJSONObject("best");
                    searchType = best.getString("type");
                    switch (searchType) {
                        case "artist":
                            parseJsonArtists(json);
                            if (artists.size() > 9)
                                artists = getArtists().subList(0, 9);
                            break;
                        case "track":
                            parseJsonTracks(json);
                            break;
                        case "playlist":
                            parseJsonPlaylists(json);
                            break;
                    }
                }
                break;

            case "track":
                parseJsonTracks(json);
                break;

            case "artist":
                parseJsonArtists(json);
                break;
            case "playlist":
                parseJsonPlaylists(json);
                break;
        }
    }

    private void parseJsonPlaylists(JSONObject json) {
        playlists = new ArrayList<>();
        JSONObject tracksRes = json.getJSONObject("playlists");
        JSONArray playlistsJson = tracksRes.getJSONArray("results");
        perPage = tracksRes.getInt("perPage");
        total = tracksRes.getInt("total");
        searchType = "playlist";
        for (int i = 0; i < playlistsJson.length(); i++) {
            JSONObject playlistJson = playlistsJson.getJSONObject(i);
            playlists.add(new YaPlaylist(playlistJson));
        }
    }

    private void parseJsonArtists(JSONObject json) {
        artists = new ArrayList<>();
        JSONObject artistsRes = json.getJSONObject("artists");
        JSONArray artistsJson = artistsRes.getJSONArray("results");
        perPage = artistsRes.getInt("perPage");
        total = artistsRes.getInt("total");
        searchType = "artist";
        for (int i = 0; i < artistsJson.length(); i++) {
            artists.add(new YaArtist(artistsJson.getJSONObject(i)));
        }
    }

    private void parseJsonTracks(JSONObject json) {
        tracks = new ArrayList<>();
        JSONObject tracksRes = json.getJSONObject("tracks");
        JSONArray tracksJson = tracksRes.getJSONArray("results");
        perPage = tracksRes.getInt("perPage");
        total = tracksRes.getInt("total");
        searchType = "track";
        for (int i = 0; i < tracksJson.length(); i++) {
            tracks.add(new YaTrack(tracksJson.getJSONObject(i)));
        }
    }

    public YaTrack getTrack(int pos) throws YaAudioException {
        if (tracks.size() > 0 || pos >= 0) {
            return tracks.get(pos);
        }
        throw new YaAudioException("Unable to get track by position");
    }
}
