package com.aqulasoft.disyam.audio;

import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlaylistState;
import com.aqulasoft.disyam.service.BotStateManager;
import com.aqulasoft.disyam.service.SecretManager;
import com.aqulasoft.disyam.utils.BotStateType;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.apache.log4j.Logger;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private AudioPlayerManager playerManager;
    private long guildId;
    private final BlockingQueue<AudioTrack> queue;
    private Random rnd = new Random();

    /**
     * @param player The audio player this scheduler uses
     * @param manager
     */

    static Logger log = Logger.getLogger(TrackScheduler.class);

    public TrackScheduler(AudioPlayer player, AudioPlayerManager manager, long guildId) {
        this.player = player;
        this.playerManager = manager;
        this.guildId = guildId;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        PlaylistState state = getYaPlaylistState();
        if (state != null) {
            loadFromPlaylist(state.next());

        } else player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    private void loadFromPlaylist(int pos) {
        PlaylistState state = getYaPlaylistState();
        if (state == null) return;
        if (pos < 0 || pos >= state.getPlaylist().getTrackCount()) return;
        YaTrack yaTrack = state.getTrack(pos);
        String url = YandexMusicManager.getTrackDownloadLink(SecretManager.get("YaToken"), yaTrack.getRealId());
        playerManager.loadItemOrdered(this, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
//                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
                player.startTrack(track, false);
                state.updateTrackMsg();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // ignored
            }

            @Override
            public void noMatches() {
                log.error("Failed to find song");
//                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                log.error("Failed to load song");
//                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    public void prevTrack() {
        PlaylistState state = getYaPlaylistState();
        if (state != null) {
            loadFromPlaylist(state.prev());
        }
    }

    private PlaylistState getYaPlaylistState() {
        BotState state = BotStateManager.getInstance().getState(guildId);
        if (state != null && state.getType() == BotStateType.YA_PLAYLIST) {
            return ((PlaylistState) state);
        }
        return null;
    }

    private boolean isYaPlaylist() {
        return BotStateManager.getInstance().getState(guildId).getType() == BotStateType.YA_PLAYLIST;
    }

}