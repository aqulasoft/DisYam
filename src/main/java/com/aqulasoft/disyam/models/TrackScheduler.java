package com.aqulasoft.disyam.models;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapter {

    final AudioPlayer player;
    public final Queue<AudioTrack> queue;
    AudioTrack lastTrack;
    private boolean repeating = false;


    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedList<>();
    }


    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void nextTrack() {
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastTrack = track;
        if (endReason.mayStartNext)
        {
            if (isRepeating())
                player.startTrack(lastTrack.makeClone(), false);
            else
                nextTrack();
        }

    }

    public boolean isRepeating()
    {
        return repeating;
    }

    public void setRepeating(boolean repeating)
    {
        this.repeating = repeating;
    }

    public void shuffle()
    {
        Collections.shuffle((List<?>) queue);
    }
}
