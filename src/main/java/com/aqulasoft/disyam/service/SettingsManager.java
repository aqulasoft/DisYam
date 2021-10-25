package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.Db.DbManager;
import com.aqulasoft.disyam.models.bot.SettingsData;
import com.aqulasoft.disyam.models.bot.SettingsState;
import com.aqulasoft.disyam.utils.SettingsStateType;
import com.aqulasoft.disyam.Db.models.SettingsDao;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

public class SettingsManager {
    private static final Map<Long, SettingsData> data = new HashMap<>();

    public static void set(Long key, SettingsData value) {
        data.put(key, value);
    }

    public static SettingsData get(Long key) {
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
            if (content.length() > 1) {
                state.updateMessage(true, true, "prefixException");
                message.delete().queue();
                return;
            }
            if (dbManager.getSettingsInfo(event.getGuild().getIdLong()) == null) {
                dbManager.insertSettings(event.getGuild().getIdLong(), content, null, null);
                SettingsData settingsData = SettingsManager.get(event.getGuild().getIdLong());
                settingsData.setPrefix(content);
            }
            if (dbManager.getSettingsInfo(event.getGuild().getIdLong()) != null) {
                dbManager.updateSettings(event.getGuild().getIdLong(), content, null, null);
                SettingsData settingsData = SettingsManager.get(event.getGuild().getIdLong());
                settingsData.setPrefix(content);
            }
            state.updateMessage(false, true, null);
            state.setSettingsType(null);
            message.delete().queue();
        } else if (state.getStateType().equals(SettingsStateType.VOLUME_STATE_TYPE)) {
            try {
                if (Integer.parseInt(content) > 100 || Integer.parseInt(content) < 0) {
                    state.updateMessage(true, true, "volumeException");
                    message.delete().queue();
                    return;
                }
            } catch (NumberFormatException e) {
                state.updateMessage(true, true, "volumeException");
                message.delete().queue();
            }
            if (dbManager.getSettingsInfo(event.getGuild().getIdLong()) == null) {
                dbManager.insertSettings(event.getGuild().getIdLong(), null, Integer.valueOf(content), null);
                SettingsData settingsData = SettingsManager.get(event.getGuild().getIdLong());
                settingsData.setVolume(Integer.valueOf(content));
            }
            if (dbManager.getSettingsInfo(event.getGuild().getIdLong()) != null) {
                dbManager.updateSettings(event.getGuild().getIdLong(), null, Integer.valueOf(content), null);
                state.updateMessage(false, true, null);
                SettingsData settingsData = SettingsManager.get(event.getGuild().getIdLong());
                settingsData.setVolume(Integer.valueOf(content));
            }
            state.setSettingsType(null);
            message.delete().queue();
        } else if (state.getStateType().equals(SettingsStateType.STATUS_STATE_TYPE)) {
            boolean status;
            if (content.equals("on")) {
                status = true;
            } else if (content.equals("off")) {
                status = false;
            } else{
                state.updateMessage(false,true,"statusTypeException");
                message.delete().queue();
                return;}
            if (dbManager.getSettingsInfo(event.getGuild().getIdLong()) == null) {
                dbManager.insertSettings(event.getGuild().getIdLong(), null, null, status);
                SettingsData settingsData = SettingsManager.get(event.getGuild().getIdLong());
                settingsData.setStatus("on");
            }
            if (dbManager.getSettingsInfo(event.getGuild().getIdLong()) != null) {
                dbManager.updateSettings(event.getGuild().getIdLong(), null, null, status);
                state.updateMessage(false, true, null);
                SettingsData settingsData = SettingsManager.get(event.getGuild().getIdLong());
                settingsData.setStatus("on");
            }
            state.setSettingsType(null);
            message.delete().queue();
        }
    }

    public static void checkAndInsertSettings(Long guildid) {
        DbManager dbManager = DbManager.getInstance();
        if (SettingsManager.get(guildid) == null) {
            SettingsData settingsData = new SettingsData();
            if (dbManager.getSettingsInfo(guildid) == null){
                dbManager.insertSettings(guildid,"!",100,false);
                settingsData.setPrefix("!");
                settingsData.setVolume(100);
            } else {
                SettingsDao settingsDao = dbManager.getSettingsInfo(guildid);
                settingsData.setPrefix(settingsDao.getPrefix());
                settingsData.setVolume(settingsDao.getValueOfVolume());
            }
            SettingsManager.set(guildid, settingsData);
        }
    }
}


