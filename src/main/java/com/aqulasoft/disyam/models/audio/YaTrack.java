package com.aqulasoft.disyam.models.audio;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class YaTrack {
    private long id;
    private long realId;
    private String title;
    private long duration;
    private List<YaArtist> artists;
//    private List<YaAlbum> albums;

    private YaTrack() {
    }

    public static YaTrack create(JSONObject json) {
        YaTrack track = new YaTrack();
        track.id = json.getLong("id");
        if (json.has("realId"))
            track.realId = json.getLong("realId");
        track.title = json.getString("title");
        track.duration = json.getLong("durationMs");
        track.artists = new ArrayList<>();
        JSONArray jsonArtistArray = json.getJSONArray("artists");
        for (int i = 0; i < jsonArtistArray.length(); i++) {
            JSONObject artist = jsonArtistArray.getJSONObject(i);
            track.artists.add(YaArtist.create(artist));
        }
        return track;
    }

    public String getFormattedArtists() {
        return artists.stream().map(YaArtist::getName).collect(Collectors.joining(", "));
    }
}


