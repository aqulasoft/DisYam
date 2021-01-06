package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.audio.YaSearchResult;
import com.aqulasoft.disyam.models.bot.ArtistSearchState;
import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlaylistSearchState;
import com.aqulasoft.disyam.models.bot.TrackSearchState;
import com.aqulasoft.disyam.service.BotStateManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

import static com.aqulasoft.disyam.utils.Consts.PREFIX;
import static com.aqulasoft.disyam.utils.Utils.joinVoice;

public class SearchCommand implements Command {
    public SearchCommand() {

    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();

        if (args.isEmpty()) {
            channel.sendMessage("Please provide some arguments").queue();
            return;
        }
        if (joinVoice(event, channel)) return;

        YaSearchResult searchResult = YandexMusicClient.search(String.join(" ", args), "all", 0);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.CYAN);
        event.getChannel().sendMessage(builder.build()).queue(message -> {
            switch (searchResult.getSearchType()) {
                case "track":
                    TrackSearchState trackState = new TrackSearchState(searchResult, message);
                    BotStateManager.getInstance().setState(event.getGuild().getIdLong(), trackState, false);
                    trackState.updateMessage(true);
                    PlayerManager playerManager = PlayerManager.getInstance();
                    playerManager.loadAndPlaySearch(event.getChannel());
                    break;

                case "artist":
                    ArtistSearchState artistState = new ArtistSearchState(searchResult, message);
                    BotStateManager.getInstance().setState(event.getGuild().getIdLong(), artistState, false);
                    artistState.updateMessage(true);
                    break;
                case "playlist":
                    PlaylistSearchState playlistState = new PlaylistSearchState(YandexMusicClient.search(searchResult.getSearchStr(), searchResult.getSearchType(), 0), message);
                    BotStateManager.getInstance().setState(event.getGuild().getIdLong(), playlistState, false);
                    playlistState.updateMessage(true);
                    break;
            }
        });
    }

    @Override
    public String getHelp() {
        return "Search best\n" +
                "Usage: `" + PREFIX + getInvoke() + " <text>`";
    }

    @Override
    public String getInvoke() {
        return "s";
    }
}
