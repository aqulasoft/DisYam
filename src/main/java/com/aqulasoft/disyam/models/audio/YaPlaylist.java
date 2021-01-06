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
    private String author;
    private String authorLogin;
    private long duration;
//    private Date modified;
    private int trackCount;
    private String description;
    private List<YaTrack> tracks;
    private String descriptionFormatted;

    private YaPlaylist() {
    }

    public static YaPlaylist create(JSONObject json) {
        YaPlaylist playlist = new YaPlaylist();
        playlist.id = json.getLong("kind");
        playlist.title = json.getString("title");
        if (json.has("description")) playlist.description = json.getString("description");
        if (json.has("descriptionFormatted")) playlist.descriptionFormatted = json.getString("descriptionFormatted");
        playlist.trackCount = json.getInt("trackCount");
        playlist.author = json.getJSONObject("owner").getString("name");
        playlist.authorLogin = json.getJSONObject("owner").getString("login");
        if (json.has("duration")) playlist.duration = json.getLong("duration");
//        playlist.modified = Date.from(Instant.parse(json.getString("modified")));
        parseTracks(json, playlist);
        return playlist;
    }

    private static void parseTracks(JSONObject json, YaPlaylist playlist) {
        if (json.has("tracks")) {
            playlist.tracks = new ArrayList<>(playlist.trackCount);
            JSONArray jsonTrackArray = json.getJSONArray("tracks");
            for (int i = 0; i < jsonTrackArray.length(); i++) {
                JSONObject track = jsonTrackArray.getJSONObject(i);
                playlist.tracks.add(YaTrack.create(track));
            }
        }
    }

    public static YaPlaylist createArtistPlaylist(JSONObject json, YaArtist artist){
        YaPlaylist playlist = new YaPlaylist();
        playlist.author = artist.getName();
        playlist.title = "Top Tracks";
        parseTracks(json, playlist);
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
