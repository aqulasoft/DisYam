package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.audio.YandexMusicManager;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.AttachmentOption;

import java.util.List;

import static com.aqulasoft.disyam.utils.Consts.PREFIX;

public class SearchCommand implements Command {

    public SearchCommand() {

    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {

        TextChannel channel = event.getChannel();



        if (args.isEmpty()) {
//            EmbedBuilder builder = new EmbedBuilder();
//            builder.addField("name", "value", true);
//            builder.addField("__name", "__value", true);
//            builder.setAuthor("me");
//            builder.setTitle("TITLE");
//            builder.setDescription("DEscription");
//            builder.setColor(Color.BLUE);
//            channel.sendMessage(builder.build()).queue(message -> {
//                message.addReaction("\uD83D\uDE42").queue();
//                message.addReaction("\uD83E\uDD37\u200D♀️").queue();
//                message.addReaction("\uD83D\uDE0E").queue();
//            });
            channel.sendMessage("Please provide some arguments").queue();
            return;
        }
        String searchResult = YandexMusicManager.search(args.get(0), "track");
        channel.sendMessage(searchResult).queue();
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
