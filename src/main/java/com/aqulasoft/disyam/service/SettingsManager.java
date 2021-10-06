package com.aqulasoft.disyam.service;

import Db.DbManager;
import com.aqulasoft.disyam.models.bot.SettingsState;
import com.aqulasoft.disyam.utils.SettingsStateType;
import models.SettingsDao;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class SettingsManager {
    private static final Map<String, String> data = new HashMap<>();

    public static void set(String key, String value) {
        data.put(key, value);
    }

    public static String get(String key) {
        return data.get(key);
    }

    public static void clear() {
        data.clear();
    }

    public static int size() {
        return data.size();
    }

    public static void InsertSettings(SettingsState state, MessageReceivedEvent event, String content) {
        DbManager dbManager = DbManager.getInstance();
        if (state.getStateType() == null) {
            return;
        }
        if (state.getStateType().equals(SettingsStateType.PREFIX_STATE_TYPE)) {
            if (dbManager.getSettingsInfo(event.getGuild().getName()) == null) {
                dbManager.insertSettings(event.getGuild().getName(), content, null, 0L);
            }
            System.out.println(content);
            if (dbManager.getSettingsInfo(event.getGuild().getName()).get(0) != null) {
                dbManager.updateSettings(event.getGuild().getName(), content, null, 0L);
                SettingsManager.set("prefix", content);
            }
            state.updateMessage(false, true, null);
            state.setSettingsType(null);
        } else if (state.getStateType().equals(SettingsStateType.VOLUME_STATE_TYPE)) {
            if (Integer.parseInt(content) > 100 && Integer.parseInt(content) < 0) {
                event.getTextChannel().sendMessage("Please enter value in [0,100]").queue();
                return;
            }
            if (dbManager.getSettingsInfo(event.getGuild().getName()) == null) {
                dbManager.insertSettings(event.getGuild().getName(), null, Integer.valueOf(content), null);
            }
            if (dbManager.getSettingsInfo(event.getGuild().getName()).get(0) != null) {
                dbManager.updateSettings(event.getGuild().getName(), null, Integer.valueOf(content), null);
            }
            state.updateMessage(false, true, null);
            SettingsManager.set("volume", content);
            state.setSettingsType(null);

        }
    }
    public static void checkAndInsertSettings(String guildName){
        DbManager dbManager = DbManager.getInstance();
        if (SettingsManager.get("prefix") == null){
            if (dbManager.getSettingsInfo(guildName)== null){
                SettingsManager.set("prefix","!");
                SettingsManager.set("volume", String.valueOf(100));
            }else{
                SettingsDao settingsDao = dbManager.getSettingsInfo(guildName).get(0);
                SettingsManager.set("prefix",settingsDao.getPrefix());
                SettingsManager.set("volume", String.valueOf(settingsDao.getValueOfVolume()));
            }
        }
    }
}


