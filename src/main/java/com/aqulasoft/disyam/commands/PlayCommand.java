package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.models.bot.PlaylistState;
import com.aqulasoft.disyam.service.BotStateManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.aqulasoft.disyam.audio.YandexMusicManager.getPlaylist;
import static com.aqulasoft.disyam.utils.Consts.PLAYLIST_URL_REGEX;
import static com.aqulasoft.disyam.utils.Consts.PREFIX;

public class PlayCommand implements Command {

    private final static Pattern playlistPattern = Pattern.compile(PLAYLIST_URL_REGEX);
    static Logger log = Logger.getLogger(PlayCommand.class);

    public PlayCommand() {
    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {

        TextChannel channel = event.getChannel();

        if (args.isEmpty()) {
            channel.sendMessage("Please provide some arguments").queue();
            return;
        }
        Matcher playlistMatcher = playlistPattern.matcher(args.get(0));

        AudioManager audioManager = event.getGuild().getAudioManager();

        if (!audioManager.isConnected()) {
            GuildVoiceState memberVoiceState = event.getMember().getVoiceState();

            if (memberVoiceState != null && !memberVoiceState.inVoiceChannel()) {
                channel.sendMessage("Please join a voice channel first").queue();
                return;
            }
            VoiceChannel voiceChannel = memberVoiceState.getChannel();
            Member selfMember = event.getGuild().getSelfMember();

            if (!selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
                channel.sendMessageFormat("I am missing permission to join %s", voiceChannel).queue();
                return;
            }

            audioManager.openAudioConnection(voiceChannel);
        }

        if (playlistMatcher.matches()) {
            String username = playlistMatcher.group(1);
            String playlistId = playlistMatcher.group(2);
            handlePlaylist(event, username, playlistId);
            return;
        }

        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.loadAndPlay(event.getChannel(), args.get(0));
    }

    private void handlePlaylist(GuildMessageReceivedEvent event, String username, String playlistId) {
        YaPlaylist playlist = getPlaylist(username, playlistId);
        YaTrack track = playlist.getTrack(0);
        if (track != null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.ORANGE);
            event.getChannel().sendMessage(builder.build()).queue(message -> {
                PlaylistState state = new PlaylistState(playlist, 0, message);
                BotStateManager.getInstance().setState(event.getGuild().getIdLong(), state);
                state.updateTrackMsg(true);
                PlayerManager playerManager = PlayerManager.getInstance();
                playerManager.loadAndPlayPlaylist(event.getChannel());
            });
        }
    }

    @Override
    public String getHelp() {
        return "Plays a song\n" +
                "Usage: `" + PREFIX + getInvoke() + " <song url>`";
    }

    @Override
    public String getInvoke() {
        return "play";
    }
}
