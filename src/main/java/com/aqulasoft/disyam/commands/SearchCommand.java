package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.audio.YandexMusicManager;
import com.aqulasoft.disyam.models.audio.YaSearchResult;
import com.aqulasoft.disyam.models.bot.PlaylistState;
import com.aqulasoft.disyam.models.bot.SearchState;
import com.aqulasoft.disyam.service.BotStateManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.AttachmentOption;

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

        YaSearchResult searchResult = YandexMusicManager.search(String.join(" ", args), "track");
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.ORANGE);
        event.getChannel().sendMessage(builder.build()).queue(message -> {
            SearchState state = new SearchState(searchResult, message);
            BotStateManager.getInstance().setState(event.getGuild().getIdLong(), state, true);
            state.updateSearchMsg(true);
            PlayerManager playerManager = PlayerManager.getInstance();
            playerManager.loadAndPlaySearch(event.getChannel());
        });
    }

    @Override
    public String getHelp() {
        return "Search a song\n" +
                "Usage: `" + PREFIX + getInvoke() + " <song url>`";
    }

    @Override
    public String getInvoke() {
        return "search";
    }
}
