package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.models.bot.BotState;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.aqulasoft.disyam.utils.Consts.INACTIVITY_MINUTE_MAX;


public class BotStateManager {

    static Logger log = Logger.getLogger(BotStateManager.class);

    private static BotStateManager INSTANCE;
    private final Map<Long, BotState> botStates;
    private final Map<Long, Date> inactivityEntities;

    public BotStateManager() {

        botStates = new HashMap<>();
        inactivityEntities = new HashMap<>();
    }

    public void setState(long guildId, BotState botState, boolean removeMessageReactions) {
        if (removeMessageReactions) {
            BotState prevState = botStates.get(guildId);
            if (prevState != null && prevState.getLastMessage() != null) {
                prevState.getLastMessage().clearReactions().queue();
            }
        }
        botStates.put(guildId, botState);
    }

    public BotState getState(long guildId) {
        return botStates.get(guildId);
    }

    public void deleteState(long guildId) {
        botStates.remove(guildId);
    }

    public static synchronized BotStateManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BotStateManager();
        }
        return INSTANCE;
    }

    public void checkInactivity() {
        Date now = new Date();
        for (Long guildId : botStates.keySet()) {
            BotState state = botStates.get(guildId);
            AudioPlayer player = PlayerManager.getInstance().getGuildMusicManager(state.getGuild()).player;
            AudioManager audioManager = state.getGuild().getAudioManager();
            AtomicBoolean isEmptyChannel = new AtomicBoolean(true);
            if (audioManager.isConnected()) {
                audioManager.getConnectedChannel().getMembers().forEach(member -> {
                    if (!member.getUser().isBot()) isEmptyChannel.set(false);
                });
            }

            if (isEmptyChannel.get()) {
                if (inactivityEntities.containsKey(guildId)) {
                    long diffInMillies = Math.abs(now.getTime() - inactivityEntities.get(guildId).getTime());
                    long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    if (diff > INACTIVITY_MINUTE_MAX) {
                        log.info(String.format("Destroyed: %s-%s", state.getGuild().getName(), audioManager.isConnected() ? audioManager.getConnectedChannel().getName() : "null"));
                        state.getLastMessage().getChannel().sendMessage("Bot has been successfully suspended.").queue();
                        audioManager.closeAudioConnection();
                        player.destroy();
                        PlayerManager.getInstance().removeMusicManager(guildId);
                        botStates.remove(guildId);
                    }
                } else {
                    log.info(String.format("Found inactivity entity: %s-%s", state.getGuild().getName(), audioManager.isConnected() ? audioManager.getConnectedChannel().getName() : "null"));
                    inactivityEntities.put(guildId, now);
                }

            } else inactivityEntities.remove(guildId);
        }
    }
}
