package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.audio.PlaylistWrongRevisionException;
import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlayerState;
import com.aqulasoft.disyam.models.dto.UserPlaylistDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistManager {
    private Map<String, UserPlaylistDto> playlists;
    private int revison;

    public void addTrackToPlaylist(String chnlName, BotState state) throws PlaylistWrongRevisionException {


        if (playlists.containsKey(chnlName)){
            UserPlaylistDto playlist = playlists.get(chnlName);
            if (state instanceof PlayerState){
                this.revison = playlist.getRevision();
                YandexMusicClient.addTrackToPlaylist(playlist.getKind(),((PlayerState) state).getCurrentTrack().getId(),playlist.getUid(), revison);
                this.revison += 1;
            }
        }else {
            YandexMusicClient.createPlaylist(chnlName);
        }
        }
    public void updatePLaylist(){
        this.playlists = new HashMap<>();
        List<UserPlaylistDto> result = YandexMusicClient.getUserPlaylists();

        for (UserPlaylistDto userPlaylistDto : result) {
            YandexMusicClient.getUserPlaylist(userPlaylistDto.getKind());
//            this.playlists.put(userPlaylistDto.getTitle(), (UserPlaylistDto) YandexMusicClient.getUserPlaylist(userPlaylistDto.getKind()));

        }


    }
}




