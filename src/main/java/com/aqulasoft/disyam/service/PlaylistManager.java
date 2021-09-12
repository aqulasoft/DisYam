package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlayerState;
import com.aqulasoft.disyam.models.dto.UserPlaylistDto;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;
import java.util.Map;

public class PlaylistManager {
    private Map<String, UserPlaylistDto> playlists;

    public PlaylistManager(){
        playlists = new HashMap<String,UserPlaylistDto>();
        for (UserPlaylistDto userPlaylistDto : YandexMusicClient.getUserPlaylists()) {
            this.playlists.put(userPlaylistDto.getTitle(),userPlaylistDto);


        }

    }
    public void addTrackToPlaylist(String chnlName, BotState state) throws JsonProcessingException {


        if (playlists.containsKey(chnlName)){
            UserPlaylistDto playlist = playlists.get(chnlName);
            if (state instanceof PlayerState){
                YandexMusicClient.addTrackToPlaylist(playlist.getKind(),((PlayerState) state).getCurrentTrack().getId(),playlist.getUid());
            }
        }else {
            YandexMusicClient.createPlaylist(chnlName);
        }
        }
    }




