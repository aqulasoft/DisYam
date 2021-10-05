package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.service.SettingsManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class PauseCommand implements Command {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.getGuildMusicManager(event.getGuild()).player.setPaused(true);
    }

    @Override
    public String getHelp() {
        return "Pause\n" +
                "Usage: `" + SettingsManager.get("prefix") + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "pause";
    }
}
