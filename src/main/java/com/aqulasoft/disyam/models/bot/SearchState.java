package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.models.audio.YaSearchResult;
import com.aqulasoft.disyam.utils.BotStateType;
import net.dv8tion.jda.api.entities.Message;

public class SearchState implements BotState {

    private final YaSearchResult searchResult;
    private Message message;

    public SearchState(YaSearchResult searchResult, Message message) {
        this.searchResult = searchResult;
        this.message = message;
    }

    @Override
    public Message getLastMessage() {
        return message;
    }

    @Override
    public BotStateType getType() {
        return BotStateType.SEARCH;
    }
}
