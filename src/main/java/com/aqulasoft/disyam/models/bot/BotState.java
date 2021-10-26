package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.utils.BotStateType;
import com.aqulasoft.disyam.utils.SettingsStateType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

public interface BotState {
    Message getMessage();
    void setMessage(Message msg);
    Guild getGuild();
    BotStateType getType();
}
