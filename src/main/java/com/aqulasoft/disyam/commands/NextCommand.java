package com.aqulasoft.disyam.commands;


import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.service.SettingsManager;
import com.aqulasoft.disyam.utils.Utils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class NextCommand implements Command {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {

        TextChannel channel = event.getChannel();
        if (!Utils.checkVoiceChannelAvailability(event, channel)) return;
        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.getGuildMusicManager(event.getGuild()).scheduler.nextTrack();
        event.getMessage().delete().queue();
    }

    @Override
    public String getHelp() {
        return "Play next song\n" + "Usage: `" + SettingsManager.get("prefix") + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "next";
    }
}
