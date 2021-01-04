package com.aqulasoft.disyam.utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

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

    public static boolean joinVoice(GuildMessageReceivedEvent event, TextChannel channel) {
        AudioManager audioManager = event.getGuild().getAudioManager();

        if (!audioManager.isConnected()) {
            GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

            if (memberVoiceState != null && !memberVoiceState.inVoiceChannel()) {
                channel.sendMessage("Please join a voice channel first").queue();
                return true;
            }
            VoiceChannel voiceChannel = memberVoiceState.getChannel();
            Member selfMember = event.getGuild().getSelfMember();

            if (!selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
                channel.sendMessageFormat("I am missing permission to join %s", voiceChannel).queue();
                return true;
            }

            audioManager.openAudioConnection(voiceChannel);
        }
        return false;
    }

    public static String convertTimePeriod(long milliseconds) {
        String seconds = padLeftZeros(String.valueOf((milliseconds / 1000 % 60)), 2);
        String minutes = padLeftZeros(String.valueOf((milliseconds / (1000 * 60) % 60)), 2);
        int h = (int) (milliseconds / (1000 * 60 * 60) % 24);
        String hours = (h > 0 ? padLeftZeros(String.valueOf(h), 2) + ":" : "");
        return String.format("%s%s:%s", hours, minutes, seconds);
    }

    public static String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }
}
