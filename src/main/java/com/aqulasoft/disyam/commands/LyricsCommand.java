package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.models.audio.YaTrackSupplement;
import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlayerState;
import com.aqulasoft.disyam.service.BotStateManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

import static com.aqulasoft.disyam.utils.Consts.EMOJI_OK;
import static com.aqulasoft.disyam.utils.Consts.PREFIX;

public class LyricsCommand implements Command {

    public LyricsCommand() {

    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {

        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        BotState state = BotStateManager.getInstance().getState(guild.getIdLong());

        if (state instanceof PlayerState) {
            event.getMessage().delete().queue();
            YaTrack track = ((PlayerState) state).getCurrentTrack();
            YaTrackSupplement supplement = YandexMusicClient.getTrackSupplement(track.getId());
            channel.sendMessage(buildLyricsMessage(track, supplement)).queue(message -> {
                message.addReaction(EMOJI_OK).queue();
            });
        }
    }

    @Override
    public String getHelp() {
        return "Get current song lyrics\n" +
                "Usage: `" + PREFIX + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "lyr";
    }

    private MessageEmbed buildLyricsMessage(YaTrack track, YaTrackSupplement supplement) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.ORANGE);
        builder.setTitle(String.format("♪ %s ♪", track.getTitle()));
        if (supplement.isHasLyrics()) {
            String lyrics = supplement.getFullLyrics().length() > 2048 ? supplement.getFullLyrics().substring(0, 2048) : supplement.getFullLyrics();
            builder.setFooter(lyrics);
        } else {
            builder.setDescription("No lyrics available.");
        }
        return builder.build();
    }
}
