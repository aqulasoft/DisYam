package com.aqulasoft.disyam.commands;


import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.service.BotStateManager;
import com.aqulasoft.disyam.service.SettingsManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

import static com.aqulasoft.disyam.utils.Consts.*;

public class StopCommand implements Command {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        AudioPlayer player = PlayerManager.getInstance().getGuildMusicManager(event.getGuild()).player;
        AudioManager audioManager = event.getGuild().getAudioManager();
        audioManager.closeAudioConnection();
        player.destroy();
        PlayerManager.getInstance().removeMusicManager(event.getGuild().getIdLong());
        BotStateManager.getInstance().deleteState(event.getGuild().getIdLong());
        event.getChannel().sendMessage(String.format("Stopped by %s", event.getMessage().getAuthor().getAsMention())).queue(message -> {
            message.addReaction(EMOJI_STOP).queue();
        });
        event.getMessage().delete().queue();
    }

    @Override
    public String getHelp() {
        return "Stop and clear queue\n" +
                "Usage: `" + SettingsManager.get("prefix") + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "stop";
    }
}
