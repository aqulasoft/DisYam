package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.audio.YaSearchResult;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.utils.BotStateType;
import com.aqulasoft.disyam.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

public class TrackSearchState extends PlayerState implements BotState {
    private YaSearchResult searchResult;
    private Message message;
    private int page = 0;

    public TrackSearchState(YaSearchResult searchResult, Message message) {
        this.message = message;
        this.searchResult = searchResult;
    }

    @Override
    public Message getLastMessage() {
        return message;
    }

    @Override
    public BotStateType getType() {
        return BotStateType.SEARCH_TRACK;
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
    public int getTrackCount() {
        return searchResult.getTotal();
    }

    @Override
    public void updateMessage(boolean b) {
        updateSearchMsg(false);
    }

    @Override
    public int next() {
        if (getPosition() + 1 >= searchResult.getPerPage()) {
            page++;
            searchResult = YandexMusicClient.search(searchResult.getSearchStr(), searchResult.getSearchType(), page);
        }
        super.next();
        return getPosition();
    }

    @Override
    public int prev() {
        if (getPosition() == 0) {
            page--;
            searchResult = YandexMusicClient.search(searchResult.getSearchStr(), searchResult.getSearchType(), page);
        }
        super.prev();
        return getPosition();
    }

    @Override
    public int getPosition() {
        return super.getPosition() - page * searchResult.getPerPage();
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

    private String getFooter() {
        String additionalInfo = (isPaused() ? "⏸ " : "▶️ ") + (isRepeatOneOn() ? "\uD83D\uDD02 " : "");
        return String.format("(%s/%s)   %s  ", getPosition() + 1 + page * searchResult.getPerPage(), searchResult.getTotal(), Utils.convertTimePeriod(getTrack(getPosition()).getDuration())) + additionalInfo;
    }
}
