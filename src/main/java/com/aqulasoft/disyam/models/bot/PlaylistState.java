package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.models.audio.YaAudioException;
import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.utils.BotStateType;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistState implements BotState {
    private final YaPlaylist playlist;
    @Setter
    @Getter
    private int position;
    private Message message;
    private List<YaTrack> shuffledTracks;
    private boolean isShuffleOn = false;
    private boolean isRepeatOneOn = false;
    private boolean isPaused = false;

    public PlaylistState(YaPlaylist playlist, int position, Message message) {
        this.playlist = playlist;
        this.position = position;
        this.message = message;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
        updateTrackMsg(false);
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

    public int prev() {
        if (isRepeatOneOn) return position;
        if (position - 1 >= 0) {
            position--;
        } else {
            throw new YaAudioException("Unable to load previous track");
        }
        if (isShuffleOn) {
            shuffledTracks = new ArrayList<>(playlist.getTracks());
            Collections.shuffle(shuffledTracks);
        } else {
            shuffledTracks = null;
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
        if (isShuffleOn) {
            shuffledTracks = new ArrayList<>(playlist.getTracks());
            Collections.shuffle(shuffledTracks);
        } else {
            shuffledTracks = null;
        }
        return position;
    }

    public void updateShuffle() {
        isShuffleOn = !isShuffleOn;
        updateTrackMsg(false);
    }

    public void updateTrackMsg(boolean addReactions) {
        message.editMessage(buildMessage(addReactions)).queue(m -> {
            message = m;
        });
    }

    public void updateRepeatOne() {
        isRepeatOneOn = !isRepeatOneOn;
        updateTrackMsg(false);
    }


    private MessageEmbed buildMessage(boolean addReactions) {
        EmbedBuilder builder = new EmbedBuilder();

        YaTrack track = getTrack(position);
        String trackTitle = "\uD83C\uDFB5   " + track.getTitle() + "  \uD83C\uDFB5";
        String trackAuthor = track.getFormattedArtists();
        builder.setTitle(trackTitle);
        builder.setDescription(trackAuthor);
        builder.setAuthor(playlist.getTitle() + (playlist.getAuthor() != null ? " by " + playlist.getAuthor() : ""));
        builder.setColor(Color.ORANGE);
        builder.setFooter(getFooter());
        if (addReactions) {
            message.addReaction("⏮️").queue();
            message.addReaction("⏯️").queue();
            message.addReaction("⏭️").queue();
            message.addReaction("\uD83D\uDD00").queue();
            message.addReaction("\uD83D\uDD02").queue();
        }
        return builder.build();
    }

    private String getFooter() {
        String additionalInfo = (isPaused ? "⏸ " : "▶️ ") + (isRepeatOneOn ? "\uD83D\uDD02 " : "") + (isShuffleOn ? "\uD83D\uDD00" : "");
        return String.format("(%s/%s)    ", position + 1, playlist.getTrackCount()) + additionalInfo;
    }
}
