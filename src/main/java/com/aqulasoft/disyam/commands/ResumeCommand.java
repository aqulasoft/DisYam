package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.audio.PlayerManager;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

import static com.aqulasoft.disyam.utils.Consts.PREFIX;

public class ResumeCommand implements Command {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.getGuildMusicManager(event.getGuild()).player.setPaused(false);
    }

    @Override
    public String getHelp() {
        return "Resume playing\n" +
                "Usage: `" + PREFIX + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "resume";
    }
}
