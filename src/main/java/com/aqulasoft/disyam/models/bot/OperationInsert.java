package com.aqulasoft.disyam.models.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperationInsert {
    private final String op;
    private final int at;
    private final List<Map<String, Long>> tracks;


    public OperationInsert(int at, long trackId, long albumId) {
        this.at = at;
        op = "insert";
        Map<String, Long> info = new HashMap<String, Long>();
        info.put("album_id", albumId);
        info.put("id",trackId);
        tracks = new ArrayList<>();
        tracks.add(info);

    }

}
