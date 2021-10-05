package com.aqulasoft.disyam.service;

import Db.DbManager;
import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.audio.PlaylistWrongRevisionException;
import com.aqulasoft.disyam.models.audio.YaArtist;
import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.models.bot.*;
import com.aqulasoft.disyam.utils.BotStateType;
import com.aqulasoft.disyam.utils.SettingsStateType;
import com.aqulasoft.disyam.utils.Utils;
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

import static com.aqulasoft.disyam.audio.YandexMusicClient.getArtistTracks;
import static com.aqulasoft.disyam.audio.YandexMusicClient.getPlaylist;
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
        BotState state = BotStateManager.getInstance().getState(event.getGuild().getIdLong());
        DbManager dbManager = DbManager.getInstance();

        if (state instanceof SettingsState) {
            if (((SettingsState) state).getStateType() == null) {
                return;
            }
            if (((SettingsState) state).getStateType().equals(SettingsStateType.PREFIX_STATE_TYPE)) {
                if (dbManager.getSettingsInfo(event.getGuild().getName()) == null) {
                    dbManager.insertSettings(event.getGuild().getName(), content, null, 0L);
                }
                System.out.println(content);
                if (dbManager.getSettingsInfo(event.getGuild().getName()).get(0) != null) {
                    dbManager.updateSettings(event.getGuild().getName(), content, null, 0L);
                    SettingsManager.set("prefix",content);
                }
                log.info(String.format("[%s] update prefix %s in %s",event.getAuthor().getName(),content,event.getGuild().getName()));
                ((SettingsState) state).updateMessage(false,true,null);
                ((SettingsState) state).setSettingsType(null);
            } else if (((SettingsState) state).getStateType().equals(SettingsStateType.VOLUME_STATE_TYPE)) {
                if (Integer.parseInt(content) > 100 && Integer.parseInt(content) < 0 ){
                    event.getTextChannel().sendMessage("Please enter value in [0,100]").queue();
                    return;
                }
                if (dbManager.getSettingsInfo(event.getGuild().getName()) == null) {
                    dbManager.insertSettings(event.getGuild().getName(), null, Integer.valueOf(content), null);
                }
                if (dbManager.getSettingsInfo(event.getGuild().getName()).get(0) != null) {
                    dbManager.updateSettings(event.getGuild().getName(), null, Integer.valueOf(content), null);
                }
                ((SettingsState) state).updateMessage(false,true,null);
                ((SettingsState) state).setSettingsType(null);
                log.info(String.format("[%s] update volume %s in %s",event.getAuthor().getName(),content,event.getGuild().getName()));

            }
        }


        if (event.isFromType(ChannelType.TEXT)) {

            TextChannel textChannel = event.getTextChannel();

            log.info(String.format("(%s)[%s]<%#s>: %s", event.getGuild().getName(), textChannel.getName(), author, content));
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
                    state.getMessage().removeReaction(EMOJI_LIKE).queue();
                    state.getMessage().addReaction(EMOJI_DISLIKE).queue();
                    String guildName = event.getGuild().getName();
                    if (state instanceof PlayerState) {
                        long id = ((PlayerState) state).getCurrentTrack().getId();
                        if (PlaylistManager.getInstance().isInPlaylist(id, guildName)) return;
                        try {
                            PlaylistManager.getInstance().addTrackToPlaylist(guildName, ((PlayerState) state).getCurrentTrack().getId());
                        } catch (PlaylistWrongRevisionException e) {
                            PlaylistManager.getInstance().updatePLaylist();
                            try {
                                PlaylistManager.getInstance().addTrackToPlaylist(guildName, ((PlayerState) state).getCurrentTrack().getId());
                            } catch (PlaylistWrongRevisionException playlistWrongRevisionException) {
                                playlistWrongRevisionException.printStackTrace();
                            }
                        }
                    }
                    log.info(String.format("[%s]: Liked song in %s", event.getUser().getName(), guildName));
                    return;
                case EMOJI_DISLIKE:
                    String serverName = event.getGuild().getName();
                    state.getMessage().removeReaction(EMOJI_DISLIKE).queue();
                    state.getMessage().addReaction(EMOJI_LIKE).queue();
                    if (state instanceof PlayerState) {
                        long id = ((PlayerState) state).getCurrentTrack().getId();
                        if (!PlaylistManager.getInstance().isInPlaylist(id, serverName)) return;
                        try {
                            PlaylistManager.getInstance().deleteTrackFromPlaylist(id, serverName);
                        } catch (PlaylistWrongRevisionException e) {
                            PlaylistManager.getInstance().updatePLaylist();
                            try {
                                PlaylistManager.getInstance().deleteTrackFromPlaylist(id, serverName);
                            } catch (PlaylistWrongRevisionException playlistWrongRevisionException) {
                                playlistWrongRevisionException.printStackTrace();
                            }
                        }
                    }
                    log.info(String.format("[%s]: Disliked song in %s", event.getUser().getName(), serverName));
                    break;
                case EMOJI_PREFIX:
                    if (state instanceof SettingsState) {
                        ((SettingsState) state).updateMessage(true, true,"prefix");
                        ((SettingsState) state).setSettingsType("prefix");
                    }
                    break;

                case EMOJI_VOLUME:
                    if (state instanceof SettingsState) {
                        ((SettingsState) state).updateMessage(true, true,"volume");
                        ((SettingsState) state).setSettingsType("volume");

                    }
                    break;
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

        if (rw.equalsIgnoreCase(SettingsManager.get("prefix") + "shutdown")) {
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

        if (!event.getAuthor().isBot() && !event.getMessage().isWebhookMessage() && rw.startsWith(SettingsManager.get("prefix"))) {
            manager.handleCommand(event);
            log.info("HANDLE");
        }
    }

    private void shutdown(JDA jda) {
        jda.shutdown();
        System.exit(0);
    }
}