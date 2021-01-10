package com.aqulasoft.disyam.models.audio;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class YaStationSequence {
    private final String batchId;
    private final YaTrack track;
    private final List<YaTrack> tracks;

    public YaStationSequence(JSONObject json, YaTrack track) {
        batchId = json.getString("batchId");
        this.track = track;
        JSONArray sequence = json.getJSONArray("sequence");
        tracks = new ArrayList<>();
        for (int i = 0; i < sequence.length(); i++) {
            JSONObject rec = sequence.getJSONObject(i);
            tracks.add(new YaTrack(rec.getJSONObject("track")));
        }
    }
}
