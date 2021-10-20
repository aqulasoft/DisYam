package com.aqulasoft.disyam.Db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.aqulasoft.disyam.Db.models.SettingsDao;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

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

    public void insertSettings(String guildName, String prefix, Integer valueOfVolume, Boolean progress) {
        SettingsDao settingsDao = new SettingsDao();
        settingsDao.setGuildName(guildName);
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

    public SettingsDao getSettingsInfo(String guildName) {
        Dao<SettingsDao, String> settingsDaos = DaoManager.lookupDao(connection, SettingsDao.class);
        try {
            return settingsDaos.queryForId(guildName);
        } catch (SQLException e) {
            log.error(e);
        }

        return null;
    }

    public void updateSettings(String guildName, String prefix, Integer valueOfVolume, Boolean progress) {
        Dao<SettingsDao, String> settingsManager = DaoManager.lookupDao(connection, SettingsDao.class);
        SettingsDao settingsDao = getSettingsInfo(guildName);
        if (settingsDao == null)return;
        if (valueOfVolume != null) {
            settingsDao.setValueOfVolume(valueOfVolume);
        }
        if (prefix != null) {
            settingsDao.setPrefix(prefix);
        }
        if(progress != null){
            settingsDao.setShowTrackProgress(progress);
        }
        try {
            settingsManager.update(settingsDao);
        } catch (SQLException e) {
            log.error(e);
        }
    }
    public void getAddedGuilds(){

    }
}