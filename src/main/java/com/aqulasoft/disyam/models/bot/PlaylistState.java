package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.utils.BotStateType;
import net.dv8tion.jda.api.entities.Message;

public class PlaylistState implements BotState {
    YaPlaylist playlist;
    int position;


    @Override
    public Message getLastMessage() {
        return null;
    }

    @Override
    public BotStateType getType() {
        return BotStateType.PLAYLIST;
    }
}
