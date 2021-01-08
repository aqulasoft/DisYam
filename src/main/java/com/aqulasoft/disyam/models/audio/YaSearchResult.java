package com.aqulasoft.disyam.models.audio;

import com.aqulasoft.disyam.audio.YandexMusicClient;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class YaSearchResult {
    private String searchStr;
    private List<YaTrack> tracks;
    private List<YaArtist> artists;
    private List<YaPlaylist> playlists;
    private int perPage;
    private int total;
    private String searchType;

    private YaSearchResult() {
    }

    public static YaSearchResult create(JSONObject json) {
        YaSearchResult res = new YaSearchResult();
        res.searchStr = json.getString("text");
        if (json.has("best")) {
            JSONObject best = json.getJSONObject("best");
            res.searchType = best.getString("type");
            switch (res.searchType) {
                case "artist":
                    parseJsonArtists(json, res);
                    if (res.artists.size() > 9)
                        res.artists = res.getArtists().subList(0, 9);
                    return res;
                case "track":
                    parseJsonTracks(json, res);
                    return res;
                case "playlist":
                    parseJsonPlaylists(json, res);
                    return res;
            }
        }

        if (json.has("tracks")) {
            parseJsonTracks(json, res);
        }

        if (json.has("artists")) {
            parseJsonArtists(json, res);
        }

        if (json.has("playlists")) {
            parseJsonPlaylists(json, res);
        }

        return res;
    }

    private static void parseJsonPlaylists(JSONObject json, YaSearchResult res) {
        res.playlists = new ArrayList<>();
        JSONObject tracksRes = json.getJSONObject("playlists");
        JSONArray playlists = tracksRes.getJSONArray("results");
        res.perPage = tracksRes.getInt("perPage");
        res.total = tracksRes.getInt("total");
        res.searchType = "playlist";
        for (int i = 0; i < playlists.length(); i++) {
            JSONObject playlistJson = playlists.getJSONObject(i);
            res.playlists.add(YaPlaylist.create(playlistJson));
        }
    }

    private static void parseJsonArtists(JSONObject json, YaSearchResult res) {
        res.artists = new ArrayList<>();
        JSONObject artistsRes = json.getJSONObject("artists");
        JSONArray artists = artistsRes.getJSONArray("results");
        res.perPage = artistsRes.getInt("perPage");
        res.total = artistsRes.getInt("total");
        res.searchType = "artist";
        for (int i = 0; i < artists.length(); i++) {
            res.artists.add(YaArtist.create(artists.getJSONObject(i)));
        }
    }

    private static void parseJsonTracks(JSONObject json, YaSearchResult res) {
        res.tracks = new ArrayList<>();
        JSONObject tracksRes = json.getJSONObject("tracks");
        JSONArray tracks = tracksRes.getJSONArray("results");
        res.perPage = tracksRes.getInt("perPage");
        res.total = tracksRes.getInt("total");
        res.searchType = "track";
        for (int i = 0; i < tracks.length(); i++) {
            res.tracks.add(YaTrack.create(tracks.getJSONObject(i)));
        }
    }

    public YaTrack getTrack(int pos) throws YaAudioException {
        if (tracks.size() > 0 || pos >= 0) {
            return tracks.get(pos);
        }
        throw new YaAudioException("Unable to get track by position");
    }
}
