package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.Db.DbManager;
import com.aqulasoft.disyam.models.bot.SettingsState;
import com.aqulasoft.disyam.utils.SettingsStateType;
import com.aqulasoft.disyam.Db.models.SettingsDao;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class SettingsManager {
    private static final Map<String, Map<String, String>> data = new HashMap<>();

    public static void set(String key, Map<String, String> value) {
        data.put(key, value);
    }

    public static Map<String, String> get(String key) {
        return data.get(key);
    }

    public static void clear() {
        data.clear();
    }

    public static int size() {
        return data.size();
    }

    public static void InsertSettings(SettingsState state, MessageReceivedEvent event, Message message) {
        String content = message.getContentDisplay();
        DbManager dbManager = DbManager.getInstance();
        if (state.getStateType() == null) {
            return;
        }
        if (state.getStateType().equals(SettingsStateType.PREFIX_STATE_TYPE)) {
            if (content.length() > 1){
                state.updateMessage(true,true,"prefixException");
                message.delete().queue();
                return;
            }
            if (dbManager.getSettingsInfo(event.getGuild().getName()) == null) {
                dbManager.insertSettings(event.getGuild().getName(), content, null, 0L);
            }
            if (dbManager.getSettingsInfo(event.getGuild().getName()) != null) {
                dbManager.updateSettings(event.getGuild().getName(), content, null, 0L);
                Map<String, String> map = new HashMap<>();
                map.put("prefix", content);
                SettingsManager.set(event.getGuild().getName(), map);
            }
            state.updateMessage(false, true, null);
            state.setSettingsType(null);
            message.delete().queue();
        } else if (state.getStateType().equals(SettingsStateType.VOLUME_STATE_TYPE)) {
            try {
                if (Integer.parseInt(content) > 200 || Integer.parseInt(content) < 0) {
                    state.updateMessage(true,true,"volumeException");
                    message.delete().queue();
                    return;
                }
            }catch (NumberFormatException e){
                state.updateMessage(true,true,"volumeException");
                message.delete().queue();
            }
            if (dbManager.getSettingsInfo(event.getGuild().getName()) == null) {
                dbManager.insertSettings(event.getGuild().getName(), null, Integer.valueOf(content), null);
            }
            if (dbManager.getSettingsInfo(event.getGuild().getName()) != null) {
                dbManager.updateSettings(event.getGuild().getName(), null, Integer.valueOf(content), null);
                state.updateMessage(false, true, null);
                Map<String, String> map = new HashMap<>();
                map.put("volume", content);
                SettingsManager.set(event.getGuild().getName(), map);
            }
            state.setSettingsType(null);
            message.delete().queue();
        }
    }

    public static void checkAndInsertSettings(String guildName) {
        DbManager dbManager = DbManager.getInstance();
        if (SettingsManager.get(guildName) == null) {
            if (dbManager.getSettingsInfo(guildName)== null) {
                Map<String, String> map = new HashMap<>();
                map.put("prefix", "!");
                map.put("volume", String.valueOf(100));
                SettingsManager.set(guildName, map);
            } else {
                SettingsDao settingsDao = dbManager.getSettingsInfo(guildName);
                Map<String, String> map = new HashMap<>();
                map.put("prefix", settingsDao.getPrefix());
                map.put("volume", String.valueOf(settingsDao.getValueOfVolume()));
                SettingsManager.set(guildName, map);
            }
        }
    }
}


