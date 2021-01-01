package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.models.bot.BotState;

public class BotStateManager {
    private BotState botState;

    public BotStateManager() {

    }

    public void setState(BotState botState) {
        this.botState = botState;
    }

    public BotState getBotState(BotState botState) {
        return botState;
    }
}
