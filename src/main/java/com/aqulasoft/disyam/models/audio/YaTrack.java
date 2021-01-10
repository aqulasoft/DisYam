package com.aqulasoft.disyam.models.audio;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class YaTrack {
    private final long id;
    private long realId;
    private final String title;
    private final long duration;
    private final List<YaArtist> artists;
    private final List<YaAlbum> albums;

    public YaTrack(JSONObject json) {
        id = json.getLong("id");
        if (json.has("realId"))
            realId = json.getLong("realId");
        title = json.getString("title");
        duration = json.getLong("durationMs");
        artists = new ArrayList<>();
        JSONArray jsonArtistArray = json.getJSONArray("artists");
        for (int i = 0; i < jsonArtistArray.length(); i++) {
            JSONObject artist = jsonArtistArray.getJSONObject(i);
            artists.add(new YaArtist(artist));
        }

        albums = new ArrayList<>();

        JSONArray jsonAlbumArray = json.getJSONArray("albums");
        for (int i = 0; i < jsonAlbumArray.length(); i++) {
            JSONObject album = jsonAlbumArray.getJSONObject(i);
            albums.add(new YaAlbum(album));
        }
    }

    public String getFormattedArtists() {
        return artists.stream().map(YaArtist::getName).collect(Collectors.joining(", "));
    }
}


