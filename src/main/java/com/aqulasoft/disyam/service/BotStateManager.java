package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlayerState;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.aqulasoft.disyam.utils.Consts.INACTIVITY_MINUTE_MAX;


public class BotStateManager {

    static Logger log = Logger.getLogger(BotStateManager.class);

    private static BotStateManager INSTANCE;
    private final Map<Long, BotState> botStates;
    private final Map<Long, BotState> botPlayerStates;
    private final Map<Long, Date> inactivityEntities;

    public BotStateManager() {
        botStates = new HashMap<>();
        inactivityEntities = new HashMap<>();
        botPlayerStates = new HashMap<>();
    }

    public void setState(long guildId, BotState state, boolean removeMessageReactions) {
        BotState prevState = botStates.get(guildId);
        if (removeMessageReactions) {
            if (prevState != null && prevState.getMessage() != null) {
                prevState.getMessage().clearReactions().queue();
            }
        }
        if (prevState instanceof PlayerState) {
            botPlayerStates.put(guildId, prevState);
        }
        botStates.put(guildId, state);
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

    public void revertPlayerState(long guildId) {
        BotState prevState = botPlayerStates.get(guildId);
        BotState curState = botStates.get(guildId);
        if (!(curState instanceof PlayerState)) {
            if (prevState instanceof PlayerState) {
                curState.getMessage().clearReactions().queue(c -> {
                    curState.getMessage().getJDA().cancelRequests();
                    prevState.getMessage().delete().queue();
                    prevState.setMessage(curState.getMessage());
                    botStates.put(guildId, prevState);
                    ((PlayerState) prevState).updateMessage(true);
                });
            } else {
                curState.getMessage().delete().queue();
                botPlayerStates.remove(guildId);
                botStates.remove(guildId);
            }
        }
    }

    public void checkInactivity() {
        Date now = new Date();
        List<Long> needsToBeFree = new ArrayList<>();
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
                        state.getMessage().getChannel().sendMessage("Bot has been successfully suspended.").queue();
                        audioManager.closeAudioConnection();
                        player.destroy();
                        PlayerManager.getInstance().removeMusicManager(guildId);
                        needsToBeFree.add(guildId);
                        botPlayerStates.remove(guildId);
                    }
                } else {
                    log.info(String.format("Found inactivity entity: %s-%s", state.getGuild().getName(), audioManager.isConnected() ? audioManager.getConnectedChannel().getName() : "null"));
                    inactivityEntities.put(guildId, now);
                }

            } else inactivityEntities.remove(guildId);
        }
        needsToBeFree.forEach(botStates::remove);
    }
}
