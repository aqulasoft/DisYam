package com.aqulasoft.disyam;

import com.aqulasoft.disyam.models.dto.UserPlaylistDto;
import com.aqulasoft.disyam.service.PlaylistManager;
import org.apache.log4j.Logger;

import java.util.List;

public class Main {
    static Logger log = Logger.getLogger(Main.class);
    public static void main(final String[] args) {
        final String token = args[0];
        final String username = args[1];
        final String password = args[2];
        DisYamBot disYamBot = new DisYamBot(token, username, password);
        PlaylistManager playlistManager = new PlaylistManager();
        playlistManager.updatePLaylist();
        log.info("Bot created");
        disYamBot.Start();
    }
}
