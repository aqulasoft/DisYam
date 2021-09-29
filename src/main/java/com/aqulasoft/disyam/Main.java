package com.aqulasoft.disyam;

import Db.DbManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;

public class Main {
    static Logger log = Logger.getLogger(Main.class);
    public static void main(final String[] args) throws SQLException {
        final String token = args[0];
        final String username = args[1];
        final String password = args[2];
//        DisYamBot disYamBot = new DisYamBot(token, username, password);
//        DbManager dbManager = DbManager.getInstance();
        log.info("Bot created");
        DbManager dbManager = DbManager.getInstance();
        dbManager.insertSettings("Golden Rain");
//        disYamBot.Start();
    }
}
