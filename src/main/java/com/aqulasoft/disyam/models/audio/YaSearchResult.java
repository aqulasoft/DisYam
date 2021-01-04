package com.aqulasoft.disyam.models.audio;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class YaSearchResult {
    private String searchStr;
    private List<YaTrack> tracks;
    private int perPage;
    private int total;
    private YaSearchResult() {
    }

    public static YaSearchResult create(JSONObject json) {
        YaSearchResult res = new YaSearchResult();
        res.searchStr = json.getString("text");
        res.tracks = new ArrayList<>();
        JSONObject tracksRes = json.getJSONObject("tracks");
        JSONArray tracks = tracksRes.getJSONArray("results");
        res.perPage = tracksRes.getInt("perPage");
        res.total = tracksRes.getInt("total");
        for (int i = 0; i < tracks.length(); i++) {
            res.tracks.add(YaTrack.create(tracks.getJSONObject(i)));
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
