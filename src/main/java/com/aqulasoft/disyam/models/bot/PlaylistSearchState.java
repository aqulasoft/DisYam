package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.audio.YandexMusicManager;
import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.models.audio.YaSearchResult;
import com.aqulasoft.disyam.utils.BotStateType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

import static com.aqulasoft.disyam.utils.Consts.EMOJI_MAP;

public class PlaylistSearchState extends SearchPager implements BotState {

    private YaSearchResult searchResult;
    private Message message;

    public PlaylistSearchState(YaSearchResult searchResult, Message message) {
        super(searchResult.getTotal(), searchResult.getPerPage());
        this.searchResult = searchResult;
        this.message = message;
    }

    @Override
    public Message getLastMessage() {
        return message;
    }

    @Override
    public BotStateType getType() {
        return BotStateType.SEARCH_PLAYLIST;
    }

    public YaPlaylist getPlaylist(int pos) {
        return searchResult.getPlaylists().get(pos);
    }

    public void updateMessage(boolean addReactions) {
        message.editMessage(buildMessage(addReactions)).queue(m -> {
            message = m;
        });
    }

    private MessageEmbed buildMessage(boolean addReactions) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.RED);
        List<YaPlaylist> playlists = searchResult.getPlaylists();

        if (playlists != null) {
            if (addReactions && hasPages()) message.addReaction("⬅️").queue();
            for (int i = 0; i < playlists.size(); i++) {
                YaPlaylist playlist = playlists.get(i);
                String emojiNum = EMOJI_MAP.get(String.valueOf(i + 1));
                builder.addField(String.format("%s %s", emojiNum, playlist.getTitle()), playlist.getAuthor(), true);
                if (addReactions) message.addReaction(emojiNum).queue();
            }
            if (addReactions && hasPages()) message.addReaction("➡️").queue();
        }
        builder.setFooter(String.format("%s-%s/%s", getPage() * getPerPage() + 1, (getPage() + 1) * getPerPage(), getTotal()));
        return builder.build();
    }

    @Override
    public void updateResults(int page) {
        searchResult = YandexMusicManager.search(searchResult.getSearchStr(), searchResult.getSearchType(), page);
        updateMessage(false);
    }
}
