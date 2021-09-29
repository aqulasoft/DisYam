package Db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import models.SettingsDao;
import org.apache.log4j.Logger;

import java.sql.SQLException;

public class DbManager {
    private ConnectionSource connection;
    private static DbManager instance = null;
    private static final String CON_STR = "jdbc:sqlite:C:/Projects/DisYam/src/main/resources/settings.db";
    static Logger log = Logger.getLogger(DbManager.class);


    public static synchronized DbManager getInstance(){
        if (instance == null)
            instance = new DbManager();
        return instance;
    }

    private DbManager(){
        try {
            this.connection = new JdbcPooledConnectionSource(CON_STR);
        }catch (SQLException e) {
            log.error(e);
        }
        try {
            TableUtils.createTableIfNotExists(connection, SettingsDao.class);
        } catch (SQLException e) {
            log.error(e);
        }
    }
    public SettingsDao insertSettings(String guildName){
        SettingsDao settingsDao = new SettingsDao();
        settingsDao.setGuildName(guildName);
        settingsDao.setPrefix("!");
        settingsDao.setValueOfVolume(60);
        try {
            Dao <SettingsDao,String> stringDao = DaoManager.createDao(connection,SettingsDao.class);
            stringDao.create(settingsDao);
            stringDao.queryForId(guildName);
        } catch (SQLException e) {
            log.error(e);
        }
        return null;
    }
}