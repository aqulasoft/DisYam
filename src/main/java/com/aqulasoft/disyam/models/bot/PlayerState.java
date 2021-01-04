package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.models.audio.YaAudioException;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.utils.Utils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
abstract public class PlayerState {
    private boolean isRepeatOneOn = false;
    private boolean isPaused = false;
    private int position;

    public boolean isRepeatOneOn() {
        return isRepeatOneOn;
    }

    public void setRepeatOneOn(boolean repeatOneOn) {
        isRepeatOneOn = repeatOneOn;
        updateMessage(false);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
        updateMessage(false);
    }

    public void updateRepeatOne() {
        isRepeatOneOn = !isRepeatOneOn;
        updateMessage(false);
    }

    public int prev() {
        if (isRepeatOneOn) return position;
        if (position - 1 >= 0) {
            position--;
        } else {
            throw new YaAudioException("Unable to load previous track");
        }
        updateMessage(false);
        return position;
    }

    public List<YaTrack> getTracks() {
        return new ArrayList<>();
    }

    public YaTrack getTrack(int pos) {
        return null;
    }

    public int next() {
        if (isRepeatOneOn) return position;
        if (position + 1 < getTracks().size()) {
            position++;
        } else {
            throw new YaAudioException("Unable to load next track");
        }
        updateMessage(false);
        return position;
    }

    public void updateMessage(boolean addReactions) {

    }

    String getFooter() {
        String additionalInfo = (isPaused ? "⏸ " : "▶️ ") + (isRepeatOneOn ? "\uD83D\uDD02 " : "");
        return String.format("(%s/%s)   %s  ", position + 1, getTracks().size(), Utils.convertTimePeriod(getTrack(position).getDuration())) + additionalInfo;
    }
}
