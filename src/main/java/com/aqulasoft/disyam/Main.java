package com.aqulasoft.disyam;

import com.aqulasoft.disyam.Db.DbManager;
import org.apache.log4j.Logger;

public class Main {
    static Logger log = Logger.getLogger(Main.class);
    public static void main(final String[] args){
        final String token = args[0];
        final String username = args[1];
        final String password = args[2];
//        DbManager dbManager = DbManager.getInstance();
//        dbManager.insertSettings("AqulaSoft","!",100, 0L);
//        dbManager.insertSettings("Golden Rain","%",200, 0L);
//        System.out.println(dbManager.getSettingsInfo("AqulaSoft").getGuildName());
//        System.out.println(dbManager.getSettingsInfo("Golden Rain").getGuildName());
        DisYamBot disYamBot = new DisYamBot(token, username, password);
        log.info("Bot created");
        disYamBot.Start();
    }
}
