package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.utils.BotStateType;
import com.aqulasoft.disyam.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistState extends PlayerState implements BotState {
    private final YaPlaylist playlist;
    @Setter
    @Getter
    private int position;
    private Message message;
    private List<YaTrack> shuffledTracks;
    private boolean isShuffleOn = false;

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

    public int prev() {
        super.prev();
        if (isShuffleOn) {
            shuffledTracks = new ArrayList<>(playlist.getTracks());
            Collections.shuffle(shuffledTracks);
        } else {
            shuffledTracks = null;
        }
        return position;
    }

    @Override
    public YaTrack getTrack(int pos) {
        return shuffledTracks != null ? shuffledTracks.get(pos) : playlist.getTrack(pos);
    }

    @Override
    public List<YaTrack> getTracks() {
        return shuffledTracks != null ? shuffledTracks : playlist.getTracks();
    }

    @Override
    public int next() {
        super.next();
        if (isShuffleOn) {
            shuffledTracks = new ArrayList<>(playlist.getTracks());
            Collections.shuffle(shuffledTracks);
        } else {
            shuffledTracks = null;
        }
        return position;
    }

    @Override
    public void updateMessage(boolean addReactions) {
        updateTrackMsg(addReactions);
    }

    public void updateShuffle() {
        isShuffleOn = !isShuffleOn;
        updateTrackMsg(false);
    }

    private void updateTrackMsg(boolean addReactions) {
        message.editMessage(buildMessage(addReactions)).queue(m -> {
            message = m;
        });
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
            message.addReaction("\uD83D\uDCE5").queue();
        }
        return builder.build();
    }

    @Override
    String getFooter() {
        String additionalInfo = (isPaused() ? "⏸ " : "▶️ ") + (isRepeatOneOn() ? "\uD83D\uDD02 " : "") + (isShuffleOn ? "\uD83D\uDD00" : "");
        return String.format("(%s/%s)   %s  ", position + 1, playlist.getTrackCount(), Utils.convertTimePeriod(getTrack(position).getDuration())) + additionalInfo;
    }
}
