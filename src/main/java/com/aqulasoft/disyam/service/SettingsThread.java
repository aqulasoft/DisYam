package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.Db.DbManager;
import com.aqulasoft.disyam.Db.models.SettingsDao;
import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.models.bot.PlayerState;
import com.aqulasoft.disyam.utils.Utils;

public class SettingsThread extends Thread {

    public SettingsThread(String threadName) {
        super(threadName);
    }

    public void run() {
        DbManager dbManager = DbManager.getInstance();
        while (true) {
            for (SettingsDao settingsDao : dbManager.getAddedGuilds()) {
                System.out.println(settingsDao.getGuildId());
                if (settingsDao.getShowTrackProgress()) {
                    PlayerState state = BotStateManager.getInstance().getPlayerState(settingsDao.getGuildId());
                    if (state != null) {
                        PlayerManager playerManager = PlayerManager.getInstance();
                        String milliseconds = Utils.convertTimePeriod(playerManager.getPosition(settingsDao.getGuildId()));
                        System.out.println(milliseconds);
                        state.updateMessage(true, String.format("%s",milliseconds));
                    }
                }
                try {
                    SettingsThread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

