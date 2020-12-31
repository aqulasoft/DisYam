package com.aqulasoft.disyam;

import com.aqulasoft.disyam.models.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import com.sedmelluq.lava.common.tools.DaemonThreadFactory;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import kong.unirest.*;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.Executors;

import static com.aqulasoft.disyam.utils.Consts.baseUrl;
import static com.aqulasoft.disyam.utils.Utils.*;

public class DisYamBot {
    private GatewayDiscordClient gateway;
    private DiscordClient client;
    private AudioPlayerManager playerManager;
    private AudioPlayer player;
    private TrackScheduler trackScheduler;
    private AudioProvider provider;
    private String YaToken;
    private String botToken;

    public DisYamBot(String botToken, String username, String password) {
        this.botToken = botToken;
        MultipartBody request = getAuthRequest(username, password);
        HttpResponse<JsonNode> res = request.asJson();
        if (res.getStatus() == 200) {
            YaToken = res.getBody().getObject().getString("access_token");
        } else {
            String capchaKey = res.getBody().getObject().getString("x_captcha_key");
            if (capchaKey != null) {
                enterCaptcha(capchaKey, username, password);
                YaToken = res.getBody().getObject().getString("access_token");
            }
        }

    }

    public void Start() {
        playerManager = new DefaultAudioPlayerManager();
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        playerManager.registerSourceManager(new HttpAudioSourceManager());

        player = playerManager.createPlayer();
        trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
        provider = new LavaPlayerAudioProvider(player);

        client = DiscordClient.create(botToken);
        gateway = client.login().block();

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            String msgText = message.getContent();
            if (msgText.length() > 0 && !event.getMessage().getAuthor().get().isBot()) {
                final MessageChannel channel = message.getChannel().block();
                if (msgText.contains("!search")) {
                    onSongSearch(event);
                }
                if (msgText.contains("!play")) {
                    onSongPlay(event);
                }
                if (msgText.contains("!download")) {
                    onSongDownload(event);
                }
                if (msgText.contains("!stop")) {
                    player.stopTrack();
                }
                if (msgText.contains("!info")) {
                    channel.createMessage(player.getPlayingTrack().getInfo().toString()).block();
                }
            }
        });
        gateway.onDisconnect().block();
    }


    private void onSongDownload(MessageCreateEvent event) {
        try {
            byte[] song = downloadSong(YaToken, Long.parseLong(event.getMessage().getContent().substring(9).trim()));

            if (song != null) {

                Mono<TextChannel> textChannel = getTextChannel(gateway, event.getMessage().getChannelId());
                textChannel
                        .flatMap(c -> c.createMessage(a -> a.addFile("song.mp3", new ByteArrayInputStream(song)))).block();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String onSongSearch(MessageCreateEvent event) {
        String searchUrl = baseUrl + "/search";
        GetRequest request = Unirest.get(searchUrl)
                .queryString("text", event.getMessage().getContent().substring(8))
                .queryString("type", "track")
                .queryString("nocorrect", false)
                .queryString("page", 0);

        return request.asJson().getBody().toPrettyString().substring(0, 1500) + "....";
    }

    private void onSongPlay(MessageCreateEvent event) {
        final Member member = event.getMember().orElse(null);
        if (member != null) {
            final VoiceState voiceState = member.getVoiceState().block();
            if (voiceState != null) {
                final VoiceChannel voiceChannel = voiceState.getChannel().block();
                if (voiceChannel != null) {
                    voiceChannel.join(spec -> spec.setProvider(provider)).block();
                }
            }
        }

        try {
            long songId = Long.parseLong(event.getMessage().getContent().substring(5).trim());
            String link = getTrackDownloadLink(YaToken, songId);

            System.out.println(link);
            playSong(link);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playSong(String url) {
        final String trackUrl;

        if (url.startsWith("<") && url.endsWith(">")) {
            trackUrl = url.substring(1, url.length() - 1);
        } else {
            trackUrl = url;
        }
        playerManager.loadItemOrdered(playerManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {

                String msg = "";
                if (player.getPlayingTrack() == null) {
                    msg = "Adding **" + track.getInfo().title + "** to the queue and starting my player.";
                } else {
                    msg = "Adding **" + track.getInfo().title + "** to the queue.";
                }
                System.out.println(msg);

                trackScheduler.queue(track);
                player.startTrack(track, true);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (url.contains("list")) {
                    List<AudioTrack> tracks = playlist.getTracks();

                    System.out.println("Adding **" + playlist.getTracks().size() + "** tracks to the queue from **" + playlist.getName() + "**");
                    tracks.forEach(trackScheduler::queue);
                }
            }

            @Override
            public void noMatches() {
                System.out.println("Invalid URL: " + trackUrl /*+ ". Please select one of the following tracks:"*/);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                System.out.println("Could not play: " + exception.getMessage());
            }
        });
    }

    private static Mono<TextChannel> getTextChannel(GatewayDiscordClient discordClient, Snowflake channel) {
        return discordClient.getChannelById(channel)
                .map(TextChannel.class::cast);
    }
}
