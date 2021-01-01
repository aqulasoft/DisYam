package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.utils.BotStateType;
import net.dv8tion.jda.api.entities.Message;

public interface BotState {
    Message getLastMessage();
    BotStateType getType();
}
