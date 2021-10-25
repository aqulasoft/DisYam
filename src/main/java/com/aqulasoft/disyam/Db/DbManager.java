package com.aqulasoft.disyam.Db;

import com.aqulasoft.disyam.models.bot.SettingsOptional;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.aqulasoft.disyam.Db.models.SettingsDao;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DbManager {
    private ConnectionSource connection;
    private static DbManager instance = null;
    private static final String CON_STR = "jdbc:sqlite:C:/Projects/DisYam/src/main/resources/settings.db";
    static Logger log = Logger.getLogger(DbManager.class);


    public static synchronized DbManager getInstance() {
        if (instance == null)
            instance = new DbManager();
        return instance;
    }

    private DbManager() {
        try {
            this.connection = new JdbcPooledConnectionSource(CON_STR);
        } catch (SQLException e) {
            log.error(e);
        }
        try {
            TableUtils.createTableIfNotExists(connection, SettingsDao.class);
        } catch (SQLException e) {
            log.error(e);
        }
    }

    public void insertSettings(Long guildId, String prefix, Integer valueOfVolume, Boolean progress) {
        SettingsDao settingsDao = new SettingsDao();
        settingsDao.setGuildId(guildId);
        settingsDao.setPrefix(prefix);
        settingsDao.setValueOfVolume(valueOfVolume);
        settingsDao.setShowTrackProgress(progress);
        try {
            Dao<SettingsDao, String> stringDao = DaoManager.createDao(connection, SettingsDao.class);
            stringDao.create(settingsDao);
        } catch (SQLException e) {
            log.error(e);
        }
    }

    public SettingsDao getSettingsInfo(Long guildId) {
        Dao<SettingsDao, Long> settingsDao = DaoManager.lookupDao(connection, SettingsDao.class);
        try {
            return settingsDao.queryForId(guildId);
        } catch (SQLException e) {
            log.error(e);
        }
        return null;
    }

    public void updateSettings(SettingsOptional settingsOptional, long guildId) {
        Dao<SettingsDao, String> settingsManager = DaoManager.lookupDao(connection, SettingsDao.class);
        SettingsDao settingsDao = getSettingsInfo(guildId);
        Optional<String> prefix= settingsOptional.getPrefix();
        prefix.ifPresent(settingsDao::setPrefix);
        Optional<Integer> volume = settingsOptional.getVolume();
        volume.ifPresent(settingsDao::setValueOfVolume);
        Optional<Boolean> showTrackPosition = settingsOptional.getShowTrackPosition();
        showTrackPosition.ifPresent(settingsDao::setShowTrackProgress);
        try {
            settingsManager.update(settingsDao);
        } catch (SQLException e) {
            log.error(e);
        }
    }

    public List<SettingsDao> getAddedGuilds() {
        Dao<SettingsDao, String> settingsDao = DaoManager.lookupDao(connection, SettingsDao.class);
        try {
            return settingsDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
