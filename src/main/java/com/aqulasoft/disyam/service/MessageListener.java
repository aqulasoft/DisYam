package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.audio.YaArtist;
import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.models.audio.YaStationSequence;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.models.bot.*;
import com.aqulasoft.disyam.utils.BotStateType;
import com.aqulasoft.disyam.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.AttachmentOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.awt.*;

import static com.aqulasoft.disyam.audio.YandexMusicClient.*;
import static com.aqulasoft.disyam.utils.Consts.*;

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
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        log.info(String.format("%s has joined the %s-%s voice", event.getMember().getUser().getName(), event.getChannelJoined().getGuild().getName(), event.getChannelJoined().getName()));
        super.onGuildVoiceJoin(event);
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        log.info(String.format("%s has left the %s-%s voice", event.getMember().getUser().getName(), event.getChannelLeft().getGuild().getName(), event.getChannelLeft().getName()));
        super.onGuildVoiceLeave(event);
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
        MessageReaction.ReactionEmote emote = event.getReactionEmote();
        if (event.getMember().getUser().isBot()) {
            return;
        }
        String username = event.getUser().getName();
        PlayerManager playerManager = PlayerManager.getInstance();
        BotState state = BotStateManager.getInstance().getState(event.getGuild().getIdLong());

        if (state == null) return;

        if (event.getMessageIdLong() != state.getMessage().getIdLong() && !emote.getEmoji().equals(EMOJI_OK)) return;

        event.getReaction().removeReaction(event.getUser()).queue();
        if (emote.isEmoji())
            switch (emote.getEmoji()) {
                case EMOJI_PREVIOUS:
                    playerManager.getGuildMusicManager(event.getGuild()).scheduler.prevTrack();
                    log.info(String.format("[%s]: previous track", username));
                    break;
                case EMOJI_PLAY_PAUSE:
                    AudioPlayer player = playerManager.getGuildMusicManager(event.getGuild()).player;
                    player.setPaused(!player.isPaused());
                    log.info(String.format("[%s]: Player is %s", username, !player.isPaused() ? "on" : "off"));
                    break;
                case EMOJI_NEXT:
                    playerManager.getGuildMusicManager(event.getGuild()).scheduler.nextTrack();
                    log.info(String.format("[%s]: next track", username));
                    break;
                case EMOJI_SHUFFLE:
                    if (state.getType() == BotStateType.YA_PLAYLIST && state instanceof PlayerState) {
                        ((PlaylistState) state).updateShuffle();
                        log.info(String.format("[%s]: shuffle", username));
                    }
                    break;
                case EMOJI_REPEAT_ONE:
                    if (state instanceof PlayerState) {
                        ((PlayerState) state).updateRepeatOne();
                        log.info(String.format("[%s]: Repeat is %s", username, ((PlayerState) state).isRepeatOneOn() ? "on" : "off"));
                    }
                    break;
                case EMOJI_DOWNLOAD:
                    if (state instanceof PlayerState) {
                        YaTrack track = ((PlayerState) state).getCurrentTrack();
                        byte[] file = YandexMusicClient.downloadSong(track.getId());
                        try {
                            event.getTextChannel().sendMessage(String.format("%s by %s", track.getTitle(), track.getFormattedArtists())).addFile(file, String.format("%s.mp3", Utils.transliterate(track.getTitle())), new AttachmentOption[0]).queue();
                            log.info(String.format("[%s]: %s by %s has been downloaded.", username, track.getTitle(), track.getFormattedArtists()));
                        } catch (Exception e) {
                            event.getTextChannel().sendMessage(String.format("File size: %d bytes. %s", file.length, e.getLocalizedMessage())).queue();
                        }
                    }
                    break;
                case EMOJI_CANCEL:
                    BotStateManager.getInstance().revertPlayerState(event.getGuild().getIdLong());
                    return;
                case EMOJI_OK:
                    event.retrieveMessage().queue(message -> {
                        if (message.getJDA().getSelfUser().getIdLong() == message.getAuthor().getIdLong())
                            message.delete().queue();
                    });
                    return;
                case EMOJI_LIKE:
                    Guild guild = event.getGuild();
                    PlaylistManager playlistManager = new PlaylistManager();
                    try {
                        playlistManager.addTrackToPlaylist(guild.getName(),state);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    log.info(String.format("[%s]: Liked song in %s", event.getUser().getName(), guild.getName()));
                    return;
            }


        if (state instanceof SearchPager) {
            switch (event.getReactionEmote().getEmoji()) {
                case "➡️":
                    ((SearchPager) state).nextPage();
                    return;
                case "⬅️":
                    ((SearchPager) state).prevPage();
                    return;
            }
        }

//        if (state instanceof )
        if (state instanceof PlaylistSearchState) {
            handlePlaylistSelect((PlaylistSearchState) state, event);
            return;
        }


        if (state instanceof ArtistSearchState) {
            handleArtistSelect((ArtistSearchState) state, event);
        }
    }

    private void handleArtistSelect(ArtistSearchState state, MessageReactionAddEvent event) {
        event.getJDA().cancelRequests();
        int num = Utils.getEmojiNum(event.getReactionEmote().getEmoji()) - 1;
        YaArtist artist = state.getArtist(num);
        YaPlaylist playlist = getArtistTracks(artist);
        playPlaylist(state, event, playlist);
        log.info(String.format("[%s]: Selected artist: %s", event.getUser().getName(), artist.getName()));
    }

    private void handlePlaylistSelect(PlaylistSearchState state, MessageReactionAddEvent event) {
        event.getJDA().cancelRequests();
        int num = Utils.getEmojiNum(event.getReactionEmote().getEmoji()) - 1;
        YaPlaylist playlist = state.getPlaylist(num);
        playlist = getPlaylist(playlist.getOwner().getLogin(), String.valueOf(playlist.getId()));
        playPlaylist(state, event, playlist);
        log.info(String.format("[%s]: Selected playlist: %s by %s", event.getUser().getName(), playlist.getTitle(), playlist.getOwner().getName()));
    }

    private void playPlaylist(BotState state, MessageReactionAddEvent event, YaPlaylist playlist) {
        YaTrack track = playlist.getTrack(0);
        if (track != null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.ORANGE);
            builder.setDescription("Loading...");
            event.getChannel().sendMessage(builder.build()).queue(message -> {
                PlaylistState playlistState = new PlaylistState(playlist, message, event.getGuild());
                state.getMessage().delete().queue();
                BotStateManager.getInstance().setState(event.getGuild().getIdLong(), playlistState, false);

                playlistState.updateMessage(true);
                PlayerManager playerManager = PlayerManager.getInstance();
                playerManager.loadAndPlayPlaylist(event.getTextChannel());
            });
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String rw = event.getMessage().getContentRaw();

        if (rw.equalsIgnoreCase(PREFIX + "shutdown")) {
            shutdown(event.getJDA());
            return;
        }

//        if (rw.equalsIgnoreCase("!queue")) {
//            TextChannel channel = event.getChannel();
//            PlayerManager playerManager = PlayerManager.getInstance();
//            GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
//            BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();
//
//            if (queue.isEmpty()) {
//                channel.sendMessage("The queue is empty").queue();
//
//                return;
//            }
//
//            int trackCount = Math.min(queue.size(), 20);
//            List<AudioTrack> tracks = new ArrayList<>(queue);
//            for (int i = 0; i < trackCount; i++) {
//                AudioTrack track = tracks.get(i);
//                AudioTrackInfo info = track.getInfo();
//            }
//
//            channel.sendMessage("builder.build()").queue();
//        }

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