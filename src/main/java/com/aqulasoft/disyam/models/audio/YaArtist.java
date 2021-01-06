package com.aqulasoft.disyam.models.audio;

import kong.unirest.json.JSONObject;
import lombok.Getter;

@Getter
public class YaArtist {
    private long id;
    private String name;

    private YaArtist() {
    }

    public static YaArtist create(JSONObject json) {
        YaArtist artist = new YaArtist();
        artist.id = json.getLong("id");
        artist.name = json.getString("name");
        return artist;
    }
}
