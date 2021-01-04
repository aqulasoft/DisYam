package com.aqulasoft.disyam.models.audio;

import com.aqulasoft.disyam.audio.YandexMusicManager;
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

        if (json.has("tracks")) {
            res.tracks = new ArrayList<>();
            JSONObject tracksRes = json.getJSONObject("tracks");
            JSONArray tracks = tracksRes.getJSONArray("results");
            res.perPage = tracksRes.getInt("perPage");
            res.total = tracksRes.getInt("total");
            for (int i = 0; i < tracks.length(); i++) {
                res.tracks.add(YaTrack.create(tracks.getJSONObject(i)));
            }
        }

        if (json.has("playlists")) {
            res.playlists = new ArrayList<>();
            JSONObject tracksRes = json.getJSONObject("playlists");
            JSONArray playlists = tracksRes.getJSONArray("results");
            res.perPage = tracksRes.getInt("perPage");
            res.total = tracksRes.getInt("total");
            for (int i = 0; i < playlists.length(); i++) {
                JSONObject playlistJson = playlists.getJSONObject(i);
//                String username = playlistJson.getJSONObject("owner").getString("login");
//                YandexMusicManager.getPlaylist(username, playlistJson.getString("kind"))
                res.playlists.add(YaPlaylist.create(playlistJson));
            }
        }

        return res;
    }

    public YaTrack getTrack(int pos) throws YaAudioException {
        if (tracks.size() > 0 || pos >= 0) {
            return tracks.get(pos);
        }
        throw new YaAudioException("Unable to get track by position");
    }

}
