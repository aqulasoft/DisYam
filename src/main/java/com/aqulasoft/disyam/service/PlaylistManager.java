package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.audio.YandexMusicClient;
import com.aqulasoft.disyam.models.audio.PlaylistWrongRevisionException;
import com.aqulasoft.disyam.models.dto.TracksPlaylistDto;
import com.aqulasoft.disyam.models.dto.UserPlaylistDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistManager {
    private static PlaylistManager INSTANCE;
    private Map<String, UserPlaylistDto> playlists;

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
            this.playlists.put(userPlaylistDto.getTitle(), YandexMusicClient.getUserPlaylist(userPlaylistDto.getKind()));
        }
    }

    public boolean isInPlaylist(long trackId, String guildName) {
        UserPlaylistDto playlist = playlists.get(guildName);

        if (playlist == null) {
            return false;
        }

        List<TracksPlaylistDto> res = playlist.getTracks();
        for (TracksPlaylistDto tracksPlaylistDto : res) {
            if (tracksPlaylistDto.getId() == trackId) {
                return true;
            }
        }
        return false;
    }

    public void deleteTrackFromPlaylist(long trackId, String guildName) throws PlaylistWrongRevisionException {
        UserPlaylistDto userPlaylist = playlists.get(guildName);
        List<TracksPlaylistDto> res = userPlaylist.getTracks();
        int index = 0;
        for (TracksPlaylistDto tracksPlaylistDto : res) {
            index ++;
            if (tracksPlaylistDto.getId() == trackId) {
                YandexMusicClient.deleteTrackFromUserPLaylist(index, userPlaylist.getKind(), userPlaylist.getRevision());
                this.updatePLaylist();
                return;
            }
        }
    }

    public String getKind(String guildName) {
        return String.valueOf(playlists.get(guildName).getKind());
    }
}
