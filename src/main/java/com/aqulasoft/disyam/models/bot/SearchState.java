package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.models.audio.YaSearchResult;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.utils.BotStateType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

public class SearchState extends PlayerState implements BotState {
    private final YaSearchResult searchResult;
    private Message message;

    public SearchState(YaSearchResult searchResult, Message message) {
        this.message = message;
        this.searchResult = searchResult;
    }

    @Override
    public Message getLastMessage() {
        return message;
    }

    @Override
    public BotStateType getType() {
        return BotStateType.SEARCH;
    }

    @Override
    public YaTrack getTrack(int pos) {
        return searchResult.getTrack(pos);
    }

    @Override
    public List<YaTrack> getTracks() {
        return searchResult.getTracks();
    }

    @Override
    public void updateMessage(boolean b) {
        updateSearchMsg(false);
    }

    public void updateSearchMsg(boolean addReactions) {
        message.editMessage(buildMessage(addReactions)).queue(m -> {
            message = m;
        });
    }

    private MessageEmbed buildMessage(boolean addReactions) {
        EmbedBuilder builder = new EmbedBuilder();
        YaTrack track = getTrack(getPosition());
        String trackTitle = "\uD83C\uDFB5   " + track.getTitle() + "  \uD83C\uDFB5";
        String trackAuthor = track.getFormattedArtists();
        builder.setTitle(trackTitle);
        builder.setDescription(trackAuthor);
//        builder.setAuthor(playlist.getTitle() + (playlist.getAuthor() != null ? " by " + playlist.getAuthor() : ""));
        builder.setColor(Color.GREEN);
        builder.setFooter(getFooter());
        if (addReactions) {
            message.addReaction("⏮️").queue();
            message.addReaction("⏯️").queue();
            message.addReaction("⏭️").queue();
            message.addReaction("\uD83D\uDD02").queue();
            message.addReaction("\uD83D\uDCE5").queue();
        }
        return builder.build();
    }

}
