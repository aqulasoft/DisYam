package com.aqulasoft.disyam.models.audio;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class YaPlaylist {
    private long id;
    private String title;
    private YaArtist owner;
    private long duration;
    //    private Date modified;
    private int trackCount;
    private String description;
    private List<YaTrack> tracks;
    private String descriptionFormatted;

    public YaPlaylist(JSONObject json) {
        id = json.getLong("kind");
        title = json.getString("title");
        if (json.has("description")) description = json.getString("description");
        if (json.has("descriptionFormatted")) descriptionFormatted = json.getString("descriptionFormatted");
        trackCount = json.getInt("trackCount");
        JSONObject ownerJson = json.getJSONObject("owner");
        owner = new YaArtist(ownerJson.getLong("uid"), ownerJson.getString("name"), ownerJson.getString("login"));
        if (json.has("duration")) duration = json.getLong("duration");
//        playlist.modified = Date.from(Instant.parse(json.getString("modified")));
        parseTracks(json);
    }

    private YaPlaylist() {

    }

    private void parseTracks(JSONObject json) {
        if (json.has("tracks")) {
            tracks = new ArrayList<>(trackCount);
            JSONArray jsonTrackArray = json.getJSONArray("tracks");
            for (int i = 0; i < jsonTrackArray.length(); i++) {
                JSONObject track = jsonTrackArray.getJSONObject(i);
                tracks.add(new YaTrack(track));
            }
        }
    }

    public static YaPlaylist createArtistPlaylist(JSONObject json, YaArtist artist) {
        YaPlaylist playlist = new YaPlaylist();
        playlist.owner = artist;
        playlist.title = "Top Tracks";
        playlist.parseTracks(json);
        playlist.trackCount = playlist.tracks.size();
        return playlist;
    }

    public YaTrack getTrack(int pos) throws YaAudioException {
        if (tracks.size() > 0 || pos >= 0) {
            return tracks.get(pos);
        }
        throw new YaAudioException("Unable to get track by position");
    }
}
