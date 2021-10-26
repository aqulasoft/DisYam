package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.audio.YaSearchResult;
import com.aqulasoft.disyam.models.bot.PlaylistSearchState;
import com.aqulasoft.disyam.service.BotStateManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

import static com.aqulasoft.disyam.utils.Utils.joinVoice;

public class SearchPlaylistCommand implements Command {
    public SearchPlaylistCommand() {
    }
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        if (args.isEmpty()) {
            channel.sendMessage("Please provide some arguments").queue();
            return;
        }
        if (joinVoice(event, channel)) return;

        YaSearchResult searchResult = YandexMusicClient.search(String.join(" ", args), "playlist", 0, 9);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.ORANGE);
        builder.setDescription("Loading...");
        event.getChannel().sendMessage(builder.build()).queue(message -> {
            PlaylistSearchState state = new PlaylistSearchState(searchResult, message, event.getGuild());
            BotStateManager.getInstance().setState(event.getGuild().getIdLong(), state, false);
            state.updateMessage(true);
        });
    }

    @Override
    public String getHelp(String prefix) {
        return "Search playlists\n" +
                "Usage: `" + prefix + getInvoke() + " <playlist name>`";
    }

    @Override
    public String getInvoke() {
        return "sp";
    }
}
