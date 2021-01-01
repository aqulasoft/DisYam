package com.aqulasoft.disyam.models.audio;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class YaPlaylist {
    private long id;
    private String title;
    private String author;
    private long duration;
    private Date modified;
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
        playlist.description = json.getString("description");
        playlist.descriptionFormatted = json.getString("descriptionFormatted");
        playlist.trackCount = json.getInt("trackCount");
        playlist.author = json.getJSONObject("owner").getString("name");
        playlist.duration = json.getLong("duration");
//        playlist.modified = Date.from(Instant.parse(json.getString("modified")));
        playlist.tracks = new ArrayList<>(playlist.trackCount);

        JSONArray jsonTrackArray = json.getJSONArray("tracks");
        for (int i = 0; i < jsonTrackArray.length(); i++) {
            JSONObject track = jsonTrackArray.getJSONObject(i);
            playlist.tracks.add(YaTrack.create(track));
        }
        return playlist;

    }
}
