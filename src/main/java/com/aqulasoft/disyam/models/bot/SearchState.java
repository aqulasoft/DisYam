package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.utils.BotStateType;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class SearchState implements BotState {
    private final List<YaTrack> tracks;
    private final long id;
    @Getter
    private final String searchText;
    @Getter
    private final int page;
    @Getter
    private final int pageSize;
    @Getter
    private final int pageCount;
    @Getter
    private final String searchType;


    public SearchState(List<YaTrack> tracks, long id, String searchText, int page, int pageSize, int pageCount, String searchType) {
        this.tracks = tracks;
        this.id = id;
        this.searchText = searchText;
        this.page = page;
        this.pageSize = pageSize;
        this.pageCount = pageCount;
        this.searchType = searchType;
    }

    @Override
    public Message getLastMessage() {
        return null;
    }

    @Override
    public BotStateType getType() {
        return null;
    }
}
