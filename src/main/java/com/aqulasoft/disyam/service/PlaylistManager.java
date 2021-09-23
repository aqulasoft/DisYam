package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.audio.PlaylistWrongRevisionException;
import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlayerState;
import com.aqulasoft.disyam.models.dto.TracksPlaylistDto;
import com.aqulasoft.disyam.models.dto.UserPlaylistDto;
import com.aqulasoft.disyam.models.dto.YaResponseDto;
import kong.unirest.GenericType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistManager {
    private static PlaylistManager INSTANCE;
    private Map<String, UserPlaylistDto> playlists;
    private int indexOfPlaylist;

    public static synchronized PlaylistManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlaylistManager();
        }
        return INSTANCE;
    }


    public void addTrackToPlaylist(String chnlName, long trackId) throws PlaylistWrongRevisionException {


        if (playlists.containsKey(chnlName)) {
            UserPlaylistDto playlist = playlists.get(chnlName);
            YandexMusicClient.addTrackToPlaylist(playlist.getKind(), trackId, playlist.getUid(), playlist.getRevision());
            TracksPlaylistDto tracksPlaylistDto = new TracksPlaylistDto();
            tracksPlaylistDto.setId(trackId);
            tracksPlaylistDto.setAlbumId(playlist.getUid());
            tracksPlaylistDto.setTimestamp("2021-09-23T22:13:08+00:00");
            playlist.getTracks().add(playlist.getTracks().size(), tracksPlaylistDto);
            playlist.setRevision(playlist.getRevision() + 1);

        } else {
            YandexMusicClient.createPlaylist(chnlName);
        }
    }

    public void updatePLaylist() {
        this.playlists = new HashMap<>();
        List<UserPlaylistDto> result = YandexMusicClient.getUserPlaylists();

        for (UserPlaylistDto userPlaylistDto : result) {
//            YandexMusicClient.getUserPlaylist(userPlaylistDto.getKind());
            this.playlists.put(userPlaylistDto.getTitle(), YandexMusicClient.getUserPlaylist(userPlaylistDto.getKind()));

        }


    }

    public boolean isInPlaylist(long trackId, String guildName) {
        // TODO: 23.09.2021 отмапить данные с помощью stream
        this.indexOfPlaylist = 0;
        List<TracksPlaylistDto> res = playlists.get(guildName).getTracks();
        for (TracksPlaylistDto tracksPlaylistDto : res) {
            indexOfPlaylist += 1;

            if (tracksPlaylistDto.getId() == trackId) {
                return true;
            }

        }
        return false;
    }

    public void deleteTrackFromPlaylist(long trackId, String guildName) throws PlaylistWrongRevisionException {
        this.indexOfPlaylist = 0;
        UserPlaylistDto userPlaylist = playlists.get(guildName);
        List<TracksPlaylistDto> res = userPlaylist.getTracks();
        for (TracksPlaylistDto tracksPlaylistDto : res) {
            indexOfPlaylist += 1;

            if (tracksPlaylistDto.getId() == trackId) {
                YandexMusicClient.deleteTrackFromUserPLaylist(indexOfPlaylist, userPlaylist.getKind(), userPlaylist.getRevision());
                this.updatePLaylist();
            }


        }
    }

    public String getKind(String guildName) {
        return String.valueOf(playlists.get(guildName).getKind());

    }


}




