package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.models.bot.BotState;

import java.util.HashMap;
import java.util.Map;

public class BotStateManager {

    private static BotStateManager INSTANCE;
    private final Map<Long, BotState> botStates;

    public BotStateManager() {
        botStates = new HashMap<>();
    }

    public void setState(long guildId, BotState botState) {
        botStates.put(guildId, botState);
    }

    public BotState getState(long guildId) {
        return botStates.get(guildId);
    }

    public void deleteState(long guildId) {
        botStates.remove(guildId);
    }

    public static synchronized BotStateManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BotStateManager();
        }
        return INSTANCE;
    }
}
