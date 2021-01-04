package com.aqulasoft.disyam.utils;

import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.models.bot.PlaylistState;
import com.aqulasoft.disyam.service.BotStateManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

public class Utils {
    public static boolean checkVoiceChannelAvailability(GuildMessageReceivedEvent event, TextChannel channel) {
        AudioManager audioManager = event.getGuild().getAudioManager();

        if (!audioManager.isConnected()) {
            channel.sendMessage("I'm not playing anything").queue();
            return false;
        }

        GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

        if (!memberVoiceState.inVoiceChannel()) {
            channel.sendMessage("Please join a voice channel first").queue();
            return false;
        }
        return true;
    }
}
