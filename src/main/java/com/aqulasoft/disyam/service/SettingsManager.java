package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.Db.DbManager;
import com.aqulasoft.disyam.models.bot.SettingsData;
import com.aqulasoft.disyam.models.bot.SettingsOptional;
import com.aqulasoft.disyam.models.bot.SettingsState;
import com.aqulasoft.disyam.Db.models.SettingsDao;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SettingsManager {
    private static final Map<Long, SettingsData> settings = new HashMap<>();

    public static void set(Long key, SettingsData value) {
        settings.put(key, value);
    }

    public static SettingsData get(Long key) {
        return settings.get(key);
    }

    public static int size() {
        return settings.size();
    }

    public static void insertSettings(SettingsState state, MessageReceivedEvent event, Message message) {
        String content = message.getContentDisplay();
        DbManager dbManager = DbManager.getInstance();
        long guildId = event.getGuild().getIdLong();
        if (state.getStateType() == null) {
            return;
        }
        switch (state.getStateType()) {
            case PREFIX_STATE_TYPE: {
                if (content.length() > 1) {
                    state.updateMessage(true, true, "prefixError");
                    message.delete().queue();
                    return;
                }

                SettingsOptional settingsOptional = new SettingsOptional();
                settingsOptional.setPrefix(Optional.of(content));
                dbManager.updateSettings(settingsOptional, guildId);
                SettingsData settingsData = SettingsManager.get(guildId);
                settingsData.setPrefix(content);
                state.updateMessage(false, true, null);
                state.setSettingsType(null);
                message.delete().queue();
                break;
            }
            case VOLUME_STATE_TYPE: {
                try {
                    if (Integer.parseInt(content) > 100 || Integer.parseInt(content) < 0) {
                        state.updateMessage(true, true, "volumeError");
                        message.delete().queue();
                        return;
                    }
                } catch (NumberFormatException e) {
                    state.updateMessage(true, true, "volumeError");
                    message.delete().queue();
                }
                SettingsOptional settingsOptional = new SettingsOptional();
                settingsOptional.setVolume(Optional.of(Integer.valueOf(content)));
                dbManager.updateSettings(settingsOptional, guildId);
                state.updateMessage(false, true, null);
                SettingsData settingsData = SettingsManager.get(guildId);
                settingsData.setVolume(Integer.valueOf(content));
                state.setSettingsType(null);
                message.delete().queue();
                break;
            }
            case STATUS_STATE_TYPE: {
                boolean status;
                if (content.equals("on")) {
                    status = true;
                } else if (content.equals("off")) {
                    status = false;
                } else {
                    state.updateMessage(false, true, "statusTypeError");
                    message.delete().queue();
                    return;
                }
                SettingsOptional settingsOptional = new SettingsOptional();
                settingsOptional.setShowTrackPosition(Optional.of(status));
                dbManager.updateSettings(settingsOptional, guildId);
                state.updateMessage(false, true, null);
                SettingsData settingsData = SettingsManager.get(guildId);
                settingsData.setStatus("on");
                state.setSettingsType(null);
                message.delete().queue();
            }
        }

    }

    public static void checkAndInsertSettings(Long guildId) {
        DbManager dbManager = DbManager.getInstance();
        if (SettingsManager.get(guildId) == null) {
            SettingsData settingsData = new SettingsData();
            if (dbManager.getSettingsInfo(guildId) == null) {
                dbManager.insertSettings(guildId, "!", 100, false);
                settingsData.setPrefix("!");
                settingsData.setVolume(100);
            } else {
                SettingsDao settingsDao = dbManager.getSettingsInfo(guildId);
                settingsData.setPrefix(settingsDao.getPrefix());
                settingsData.setVolume(settingsDao.getValueOfVolume());
            }
            SettingsManager.set(guildId, settingsData);
        }
    }
}


