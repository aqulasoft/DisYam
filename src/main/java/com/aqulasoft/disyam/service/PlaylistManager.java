package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.dto.UserPlaylistDto;

import java.util.Map;

public class PlaylistManager {
    private Map<String, UserPlaylistDto> playlists;

    public PlaylistManager(){
        for (UserPlaylistDto userPlaylistDto : YandexMusicClient.getUserPlaylists()) {
            playlists.put(userPlaylistDto.getTitle(),userPlaylistDto);


        }

    }
    public void addTrackToPlaylist(String chnlName){
        if (playlists.containsKey(chnlName)){
            UserPlaylistDto playlist = playlists.get(chnlName);
//            YandexMusicClient.addTrackToPlaylist(playlist.getKind());
        }else {
//            YandexMusicClient.createPlaylist(chnlName);
        }
        }
    }




