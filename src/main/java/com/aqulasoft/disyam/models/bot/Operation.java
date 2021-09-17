package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.models.dto.UserPlaylistDto;
import com.aqulasoft.disyam.utils.PlaylistOperation;
import com.google.gson.JsonObject;
import kong.unirest.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operation {
//    private final PlaylistOperation playlistOperation;
    private final String op;
    private final int at;
    private final List<Map<String, Long>> tracks;


    public Operation(int at, long trackId, long albumId) {
        this.at = at;
//        playlistOperation = PlaylistOperation.valueOf("INSERT");
        op = "insert";
        Map<String, Long> info = new HashMap<String, Long>();
        info.put("album_id", albumId);
        info.put("id",trackId);
        tracks = new ArrayList<>();
        tracks.add(info);

    }

}
