package com.aqulasoft.disyam.models.audio;

import kong.unirest.json.JSONObject;
import lombok.Getter;

@Getter
public class YaArtist {
    private final long id;
    private final String name;
    private String login;

    public YaArtist(long id, String name, String login) {
        this.id = id;
        this.name = name;
        this.login = login;
    }

    public YaArtist(JSONObject json) {
        id = json.getLong("id");
        name = json.getString("name");
        if (json.has("login")) login = json.getString("login");
    }

}
