package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.audio.PlaylistWrongRevisionException;
import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlayerState;
import com.aqulasoft.disyam.models.dto.TracksPlaylistDto;
import com.aqulasoft.disyam.models.dto.UserPlaylistDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistManager {
    private static PlaylistManager INSTANCE;
    private Map<String, UserPlaylistDto> playlists;
    private int revison;
    private int indexOfPlaylist;

    public static synchronized PlaylistManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlaylistManager();
        }
        return INSTANCE;
    }


    public void addTrackToPlaylist(String chnlName, BotState state) throws PlaylistWrongRevisionException {


        if (playlists.containsKey(chnlName)){
            UserPlaylistDto playlist = playlists.get(chnlName);
            if (state instanceof PlayerState){
                this.revison = playlist.getRevision();
                YandexMusicClient.addTrackToPlaylist(playlist.getKind(),((PlayerState) state).getCurrentTrack().getId(),playlist.getUid(), revison);
                this.revison += 1;
                this.updatePLaylist();
            }
        }else {
            YandexMusicClient.createPlaylist(chnlName);
        }
        }
    public void updatePLaylist(){
        this.playlists = new HashMap<>();
        List<UserPlaylistDto> result = YandexMusicClient.getUserPlaylists();

        for (UserPlaylistDto userPlaylistDto : result) {
//            YandexMusicClient.getUserPlaylist(userPlaylistDto.getKind());
            this.playlists.put(userPlaylistDto.getTitle(), YandexMusicClient.getUserPlaylist(userPlaylistDto.getKind()));

        }




    }
    public boolean isInPlaylist(long trackId, String guildName ){
        // TODO: 23.09.2021 отмапить данные с помощью stream
        this.indexOfPlaylist = 0;
        System.out.println(playlists.get(guildName).getTracks());
        List<TracksPlaylistDto> res = playlists.get(guildName).getTracks();
        for (TracksPlaylistDto tracksPlaylistDto:res){
            indexOfPlaylist += 1;

            if (tracksPlaylistDto.getId() == trackId){
                System.out.println(indexOfPlaylist);
                return true;
            }

        }
        return false;
    }
    public void DeleteTrackFromPlaylist(long trackId,String guildName){
        this.indexOfPlaylist = 0;
        List<TracksPlaylistDto> res = playlists.get(guildName).getTracks();
        for (TracksPlaylistDto tracksPlaylistDto:res){
            indexOfPlaylist += 1;

            if (tracksPlaylistDto.getId() == trackId){

            }


            }
    }

}




