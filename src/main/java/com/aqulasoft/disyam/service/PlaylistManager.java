package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.audio.YandexMusicClient;
import kong.unirest.HttpResponse;

public class PlaylistManager {
    public String getPlaylist(){
        return YandexMusicClient.downloadPlaylists();

    }

}