package com.aqulasoft.disyam.commands;


import com.aqulasoft.disyam.audio.PlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;

public class ClearCommand implements Command {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {

        TextChannel channel = event.getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();

        if (!audioManager.isConnected()) {
            channel.sendMessage("I'm not playing anything").queue();
            return;
        }

        GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("Please join a voice channel first").queue();
            return;
        }
        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.getGuildMusicManager(event.getGuild()).scheduler.clear();
    }

    @Override
    public String getHelp() {
        return "Start next song";
    }

    @Override
    public String getInvoke() {
        return "next";
    }
}
