package com.aqulasoft.disyam.audio;

import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.service.SecretManager;
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
    private final BlockingQueue<AudioTrack> queue;
    private YaPlaylist playlist;
    private boolean shuffleOn = false;
    private int position;
    private Random rnd = new Random();

    /**
     * @param player The audio player this scheduler uses
     * @param manager
     */

    static Logger log = Logger.getLogger(TrackScheduler.class);

    public TrackScheduler(AudioPlayer player, AudioPlayerManager manager) {
        this.player = player;
        this.playerManager = manager;
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
        playlist = null;
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void queue(YaPlaylist playlist) {
        this.playlist = playlist;
        position = 0;
        loadFromPlaylist(position);
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
        if (playlist != null) {
            loadFromPlaylist(shuffleOn ? rnd.nextInt(playlist.getTracks().size()) : ++position);
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
        if (pos < 0 || pos >= playlist.getTracks().size()) return;
        YaTrack track = playlist.getTracks().get(pos);
        String url = YandexMusicManager.getTrackDownloadLink(SecretManager.get("YaToken"), track.getRealId());
        playerManager.loadItemOrdered(this, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
//                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
                player.startTrack(track, false);
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
        if (playlist != null) {
            --position;
            if (position < 0 ) {
                position = 0;
            }
            loadFromPlaylist(position);
        }
    }

    public void clear() {
        queue.clear();
        position = 0;
        playlist = null;
    }

    public void shuffle() {
        shuffleOn = !shuffleOn;
    }
}