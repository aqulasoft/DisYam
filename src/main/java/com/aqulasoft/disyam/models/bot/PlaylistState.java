package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.models.audio.YaAudioException;
import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.utils.BotStateType;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistState implements BotState {
    private final YaPlaylist playlist;
    @Setter
    private int position;
    private final Message message;
    private List<YaTrack> shuffledTracks;
    private boolean isShuffleOn = false;
    private boolean isRepeatOneOn = false;

    public PlaylistState(YaPlaylist playlist, int position, Message message) {
        this.playlist = playlist;
        this.position = position;
        this.message = message;
    }


    @Override
    public Message getLastMessage() {
        return message;
    }

    @Override
    public BotStateType getType() {
        return BotStateType.YA_PLAYLIST;
    }

    public YaPlaylist getPlaylist() {
        return playlist;
    }

    public int getPosition() {
        return position;
    }

    public int prev() {
        if (isRepeatOneOn) return position;
        if (position - 1 >= 0) {
            position--;
        } else {
            throw new YaAudioException("Unable to load previous track");
        }
        return position;
    }

    public YaTrack getTrack(int pos) {
        return shuffledTracks != null ? shuffledTracks.get(pos) : playlist.getTrack(pos);
    }

    public int next() {
        if (isRepeatOneOn) return position;
        if (position + 1 < playlist.getTrackCount()) {
            position++;
        } else {
            throw new YaAudioException("Unable to load next track");
        }
        return position;
    }

    public void updateShuffle() {
        if (shuffledTracks == null) {
            shuffledTracks = new ArrayList<>(playlist.getTracks());
            Collections.shuffle(shuffledTracks);
            isShuffleOn = true;
        } else {
            isShuffleOn = false;
            shuffledTracks = null;
        }
    }

    public void updateTrackMsg() {
        String additionalInfo = (isRepeatOneOn ? "\uD83D\uDD02  " : "") + (isShuffleOn ? "\uD83D\uDD00" : "");
        YaTrack track = getTrack(position);
        EmbedBuilder builder = new EmbedBuilder();
        message.addReaction("\uD83D\uDD00").queue();
        message.addReaction("\uD83D\uDD02").queue();
        builder.setTitle("\uD83C\uDFB5   " + track.getTitle() + "  \uD83C\uDFB5");
        builder.setDescription(track.getFormattedArtists() + (additionalInfo.length() > 0 ? "\n\n" + additionalInfo : ""));
        builder.setAuthor(playlist.getDescriptionFormatted() + (playlist.getAuthor() != null ? " by " + playlist.getAuthor() : ""));
        builder.setColor(Color.ORANGE);
        if (message != null) message.editMessage(builder.build()).queue();
    }

    public void updateRepeatOne() {
        isRepeatOneOn = !isRepeatOneOn;
    }
}
