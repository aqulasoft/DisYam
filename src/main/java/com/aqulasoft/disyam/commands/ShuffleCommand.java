package com.aqulasoft.disyam.commands;


import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.utils.Utils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class ShuffleCommand implements Command {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {

        TextChannel channel = event.getChannel();
        if (!Utils.checkVoiceChannelAvailability(event, channel)) return;
        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.getGuildMusicManager(event.getGuild()).scheduler.shuffle();
    }

    @Override
    public String getHelp() {
        return "Shuffle songs";
    }

    @Override
    public String getInvoke() {
        return "shuffle";
    }
}
