package com.aqulasoft.disyam.models.audio;

import kong.unirest.json.JSONObject;
import lombok.Getter;

@Getter
public class YaTrackSupplement {
    private final long id;
    private final boolean hasLyrics;
    private String lyrics;
    private String fullLyrics;
    private String textLanguage;

    public YaTrackSupplement(JSONObject json) {
        id = json.getLong("id");
        hasLyrics = json.has("lyrics");
        if (hasLyrics) {
            JSONObject lyricsJson = json.getJSONObject("lyrics");
            if (lyricsJson.has("lyrics")) lyrics = lyricsJson.getString("lyrics");
            if (lyricsJson.has("fullLyrics")) fullLyrics = lyricsJson.getString("fullLyrics");
            if (lyricsJson.has("textLanguage")) textLanguage = lyricsJson.getString("textLanguage");
        }
    }
}
