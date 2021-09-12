package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.utils.PlaylistOperation;
import com.google.gson.JsonObject;
import kong.unirest.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Operation {
    private final PlaylistOperation playlistOperation;
    private final int at;
    private final List<Map<String, Integer>> tracks;
    long trackId;
    long albumId;

    public Operation(int at, long trackId, long albumId) {
        playlistOperation = PlaylistOperation.INSERT;
        tracks = new ArrayList<>();
        this.at = at;
        this.trackId = trackId;
        this.albumId = albumId;


    }

}
