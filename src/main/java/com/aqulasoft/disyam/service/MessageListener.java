package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.audio.GuildMusicManager;
import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.audio.YandexMusicManager;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlayerState;
import com.aqulasoft.disyam.models.bot.PlaylistState;
import com.aqulasoft.disyam.utils.BotStateType;
import com.aqulasoft.disyam.utils.Utils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static com.aqulasoft.disyam.utils.Consts.PREFIX;

public class MessageListener extends ListenerAdapter {

    private final CommandManager manager;
    private final Logger log = LoggerFactory.getLogger(MessageListener.class);

    public MessageListener(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void onReady(ReadyEvent event) {
        log.info(String.format("Logged in as %#s", event.getJDA().getSelfUser()));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        User author = event.getAuthor();
        Message message = event.getMessage();
        String content = message.getContentDisplay();

        if (event.isFromType(ChannelType.TEXT)) {

            Guild guild = event.getGuild();
            TextChannel textChannel = event.getTextChannel();

            log.info(String.format("(%s)[%s]<%#s>: %s", guild.getName(), textChannel.getName(), author, content));
        } else if (event.isFromType(ChannelType.PRIVATE)) {
            log.info(String.format("[PRIV]<%#s>: %s", author, content));
        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
        if (event.getMember().getUser().isBot()) return;
        event.getReaction().removeReaction(event.getUser()).queue();
        PlayerManager playerManager = PlayerManager.getInstance();
        BotState state = BotStateManager.getInstance().getState(event.getGuild().getIdLong());
        if(event.getMessageIdLong() != state.getLastMessage().getIdLong()) return;
        switch (event.getReactionEmote().getEmoji()) {
            case "⏮️":
                playerManager.getGuildMusicManager(event.getGuild()).scheduler.prevTrack();
                break;
            case "⏯️":
                AudioPlayer player = playerManager.getGuildMusicManager(event.getGuild()).player;
                player.setPaused(!player.isPaused());
                break;
            case "⏭️":
                playerManager.getGuildMusicManager(event.getGuild()).scheduler.nextTrack();
                break;
            case "\uD83D\uDD00":
                if (state != null && state.getType() == BotStateType.YA_PLAYLIST) {
                    ((PlaylistState) state).updateShuffle();
                }
                break;
            case "\uD83D\uDD02":
                if (state != null && (state.getType() == BotStateType.YA_PLAYLIST || state.getType() == BotStateType.SEARCH)) {
                    ((PlayerState) state).updateRepeatOne();
                }
                break;
            case "\uD83D\uDCE5":
                if (state != null && (state.getType() == BotStateType.YA_PLAYLIST || state.getType() == BotStateType.SEARCH)) {
                    YaTrack track = ((PlayerState) state).getCurrentTrack();
                    byte[] file = YandexMusicManager.downloadSong(track.getId());
                    try {
                        event.getTextChannel().sendMessage(String.format("%s by %s", track.getTitle(), track.getFormattedArtists())).addFile(file, String.format("%s.mp3", Utils.transliterate(track.getTitle())), new AttachmentOption[0]).queue();
                    } catch (Exception e) {
                        event.getTextChannel().sendMessage(String.format("File size: %d bytes. %s", file.length, e.getLocalizedMessage())).queue();
                    }
                }
                break;

        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String rw = event.getMessage().getContentRaw();

        if (rw.equalsIgnoreCase(PREFIX + "shutdown")) {
            shutdown(event.getJDA());
            return;
        }

        if (rw.equalsIgnoreCase("!queue")) {
            TextChannel channel = event.getChannel();
            PlayerManager playerManager = PlayerManager.getInstance();
            GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
            BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();

            if (queue.isEmpty()) {
                channel.sendMessage("The queue is empty").queue();

                return;
            }

            int trackCount = Math.min(queue.size(), 20);
            List<AudioTrack> tracks = new ArrayList<>(queue);
            for (int i = 0; i < trackCount; i++) {
                AudioTrack track = tracks.get(i);
                AudioTrackInfo info = track.getInfo();
            }

            channel.sendMessage("builder.build()").queue();
        }

        if (!event.getAuthor().isBot() && !event.getMessage().isWebhookMessage() && rw.startsWith(PREFIX)) {
            manager.handleCommand(event);
            log.info("HANDLE");
        }
    }

    private void shutdown(JDA jda) {
        jda.shutdown();
        System.exit(0);
    }
}