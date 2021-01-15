package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlayerState;
import com.aqulasoft.disyam.service.BotStateManager;
import com.aqulasoft.disyam.utils.Utils;
import com.google.common.base.Strings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.aqulasoft.disyam.utils.Consts.EMOJI_OK;
import static com.aqulasoft.disyam.utils.Consts.PREFIX;

public class QueueInfoCommand implements Command {

    public QueueInfoCommand() {

    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {

        TextChannel channel = event.getChannel();
        Guild guild = event.getGuild();
        BotState state = BotStateManager.getInstance().getState(guild.getIdLong());

        if (state instanceof PlayerState) {
            event.getMessage().delete().queue();
            channel.sendMessage(buildQueueInfoMessage((PlayerState) state)).queue(message -> {
                message.addReaction(EMOJI_OK).queue();
            });
        }
    }

    @Override
    public String getHelp() {
        return "Get current playlists queue info\n" +
                "Usage: `" + PREFIX + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "queue";
    }

    private MessageEmbed buildQueueInfoMessage(PlayerState state) {
        EmbedBuilder builder = new EmbedBuilder();
        StringBuilder msg = new StringBuilder();
        AtomicInteger counter = new AtomicInteger(0);
        List<YaTrack> prevTracks = state.getPrevTracks(10);
        prevTracks.forEach(track -> {
            msg.append(getTrackLine(state.getPosition() - prevTracks.size() + counter.getAndIncrement() + 1, track));
        });

        msg.append("->").append(getTrackLine(state.getPosition() + 1, state.getCurrentTrack()));

        counter.set(2);

        state.getNextTracks(10).forEach(track -> {
            msg.append(getTrackLine(state.getPosition() + counter.getAndIncrement(), track));
        });
        builder.setFooter(Utils.trimString(msg.toString(), 2048));
        builder.setColor(Color.yellow);
        return builder.build();
    }

    private String getTrackLine(int counter, YaTrack track) {
//        String duration = Utils.convertTimePeriod(track.getDuration());
//        int maxInfoLen = 50 - duration.length();
        String trackInfo = String.format("%s - %s", track.getTitle(), track.getFormattedArtists());
//        if (trackInfo.length() > maxInfoLen) trackInfo = trackInfo.substring(0, maxInfoLen - 3) + "...";
//        else trackInfo = Utils.padLeftZeros(trackInfo, maxInfoLen);
        return String.format("%s) %s %s\n", Strings.padStart(String.valueOf(counter), 2, ' '), trackInfo, Utils.convertTimePeriod(track.getDuration()));
    }
}
