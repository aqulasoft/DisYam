package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.utils.BotStateType;
import com.aqulasoft.disyam.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.aqulasoft.disyam.utils.Consts.*;

public class PlaylistState extends PlayerState implements BotState {
    @Getter
    private final YaPlaylist playlist;
    @Setter
    @Getter
    private Message message;
    private List<YaTrack> shuffledTracks;
    private boolean isShuffleOn = false;
    @Getter
    private final Guild guild;

    public PlaylistState(YaPlaylist playlist, Message message, Guild guild) {
        this.playlist = playlist;
        this.message = message;
        this.guild = guild;
    }

    @Override
    public BotStateType getType() {
        return BotStateType.YA_PLAYLIST;
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
    public int getTrackCount() {
        return getTracks().size();
    }

    public void updateMessage(boolean addReactions) {
        updateMessage(addReactions, true);
    }

    public void updateMessage(boolean addReactions, boolean updateTrackInfo) {
        message.editMessage(buildMessage(addReactions, updateTrackInfo)).queue(m -> {
            message = m;
        });
    }

    public void updateShuffle() {
        isShuffleOn = !isShuffleOn;
        updateMessage(false, false);
        if (isShuffleOn) {
            shuffledTracks = new ArrayList<>(playlist.getTracks());
            Collections.shuffle(shuffledTracks);
        } else {
            shuffledTracks = null;
        }
    }

    private MessageEmbed buildMessage(boolean addReactions, boolean updateTrackInfo) {
        EmbedBuilder builder = new EmbedBuilder();

        String trackTitle = "";
        String trackAuthor = "";
        String footer = "";

        if (updateTrackInfo) {
            YaTrack track = getTrack(getPosition());
            trackTitle = "\uD83C\uDFB5   " + track.getTitle() + "  \uD83C\uDFB5";
            trackAuthor = track.getFormattedArtists();
            footer = getFooter();
        } else if (message.getEmbeds().size() > 0) {
            MessageEmbed prevMsg = message.getEmbeds().get(0);
            trackTitle = prevMsg.getTitle();
            trackAuthor = prevMsg.getDescription();
            footer = prevMsg.getFooter().getText();
            if (isShuffleOn) {
                footer += EMOJI_SHUFFLE;
            } else {
                footer = footer.replaceAll(EMOJI_SHUFFLE, "");
            }
        }
        message.getEmbeds().get(0).getTitle();
        builder.setTitle(trackTitle);
        builder.setDescription(trackAuthor);
        builder.setAuthor(playlist.getTitle() + (playlist.getOwner().getName() != null ? " by " + playlist.getOwner().getName() : ""));
        builder.setColor(Color.ORANGE);
        builder.setFooter(footer);
        if (addReactions) {
            message.addReaction(EMOJI_PREVIOUS).queue();
            message.addReaction(EMOJI_PLAY_PAUSE).queue();
            message.addReaction(EMOJI_NEXT).queue();
            message.addReaction(EMOJI_SHUFFLE).queue();
            message.addReaction(EMOJI_REPEAT_ONE).queue();
            message.addReaction(EMOJI_DOWNLOAD).queue();
        }
        return builder.build();
    }

    String getFooter() {
        String additionalInfo = (isPaused() ? "⏸ " : "▶️ ") + (isRepeatOneOn() ? "\uD83D\uDD02 " : "") + (isShuffleOn ? EMOJI_SHUFFLE : "");
        return String.format("(%s/%s)   %s  ", getPosition() + 1, playlist.getTrackCount(), Utils.convertTimePeriod(getTrack(getPosition()).getDuration())) + additionalInfo;
    }
}
