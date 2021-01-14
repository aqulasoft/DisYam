package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.audio.YaStationSequence;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.utils.BotStateType;
import com.aqulasoft.disyam.utils.Utils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.List;

import static com.aqulasoft.disyam.utils.Consts.*;

public class StationState extends PlayerState implements BotState {

    static Logger log = Logger.getLogger(StationState.class);

    private YaStationSequence sequence;
    private final List<YaTrack> tracks;
    @Getter
    @Setter
    private Message message;
    @Getter
    private final Guild guild;

    public StationState(YaStationSequence seq, Message message, Guild guild) {
        sequence = seq;
        tracks = seq.getTracks();
        this.message = message;
        this.guild = guild;
        YandexMusicClient.sendStationFeedback(String.format("track:%s", sequence.getTrack().getId()), "radioStarted", sequence.getBatchId(), null, null);
        YandexMusicClient.sendStationFeedback(String.format("track:%s", sequence.getTrack().getId()), "trackStarted", sequence.getBatchId(), getCurrentTrack().getId(), 0L);
        YandexMusicClient.playAudio(getCurrentTrack());
    }

    @Override
    public BotStateType getType() {
        return BotStateType.SEARCH_TRACK;
    }

    @Override
    public YaTrack getTrack(int pos) {
        return tracks.get(pos);
    }

    @Override
    public List<YaTrack> getTracks() {
        return tracks;
    }

    @Override
    public int getTrackCount() {
        return tracks.size() + 1; // +1 to not throw exception
    }

    @Override
    public void updateMessage(boolean addReactions) {
        message.editMessage(buildMessage(addReactions)).queue(m -> {
            message = m;
        });
    }

    @Override
    public int next() {
        long curTrackId = getCurrentTrack().getId();
        AudioTrack playingTrack = PlayerManager.getInstance().getGuildMusicManager(guild).player.getPlayingTrack();
        long duration = playingTrack != null ? playingTrack.getPosition() : getCurrentTrack().getDuration();
        String actionType = duration > getCurrentTrack().getDuration() * 0.8 ? "trackFinished" : "skip";
        log.info(actionType);
        YandexMusicClient.sendStationFeedback(String.format("track:%s", sequence.getTrack().getId()), actionType, sequence.getBatchId(), curTrackId, duration);
        super.next();
        if (getPosition() >= tracks.size()) {
            sequence = YandexMusicClient.getStationTracks(sequence.getTrack(), curTrackId);
            tracks.addAll(sequence.getTracks());
        }
        YandexMusicClient.playAudio(getCurrentTrack());
        YandexMusicClient.sendStationFeedback(String.format("track:%s", sequence.getTrack().getId()), "trackStarted", sequence.getBatchId(), getCurrentTrack().getId(), 0L);
        return getPosition();
    }

    @Override
    public int prev() {
        return super.prev();
    }

    private MessageEmbed buildMessage(boolean addReactions) {
        EmbedBuilder builder = new EmbedBuilder();
        YaTrack track = getTrack(getPosition());
        String trackTitle = "\uD83C\uDFB5   " + track.getTitle() + "  \uD83C\uDFB5";
        String trackAuthor = track.getFormattedArtists();
        builder.setTitle(trackTitle);
        builder.setDescription(trackAuthor);
        builder.setColor(Color.GREEN);
        builder.setFooter(getFooter());
        if (addReactions) {
            message.addReaction(EMOJI_PREVIOUS).queue();
            message.addReaction(EMOJI_PLAY_PAUSE).queue();
            message.addReaction(EMOJI_NEXT).queue();
            message.addReaction(EMOJI_REPEAT_ONE).queue();
            message.addReaction(EMOJI_DOWNLOAD).queue();
        }
        return builder.build();
    }

    private String getFooter() {
        YaTrack track = sequence.getTrack();
        String additionalInfo = (isPaused() ? "  ⏸ " : "  ▶️ ") + (isRepeatOneOn() ? "\uD83D\uDD02 " : "");
        String stationInfo = String.format("station by track \"%s\" by %s", track.getTitle(), track.getFormattedArtists());
        return String.format("(%s/∞)    %s \n%s", getPosition() + 1, Utils.convertTimePeriod(getTrack(getPosition()).getDuration()) + additionalInfo, stationInfo);
    }
}
