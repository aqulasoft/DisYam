package com.aqulasoft.disyam.audio;

import kong.unirest.GetRequest;
import kong.unirest.Unirest;

import static com.aqulasoft.disyam.utils.Consts.baseUrl;

public class YandexMusicManager {
//    private void onSongDownload(id) {
//        try {
//            byte[] song = downloadSong(YaToken, Long.parseLong(event.getMessage().getContent().substring(9).trim()));
//
//            if (song != null) {
//
//                Mono<TextChannel> textChannel = getTextChannel(gateway, event.getMessage().getChannelId());
//                textChannel
//                        .flatMap(c -> c.createMessage(a -> a.addFile("song.mp3", new ByteArrayInputStream(song)))).block();
//            }
//        } catch (Exception e) {
//            log.error(e);
//        }
//    }

    private String onSongSearch(String searchStr) {
        String searchUrl = baseUrl + "/search";
        GetRequest request = Unirest.get(searchUrl)
                .queryString("text", searchStr.substring(8))
                .queryString("type", "track")
                .queryString("nocorrect", false)
                .queryString("page", 0);

        return request.asJson().getBody().toPrettyString().substring(0, 1500) + "....";
    }
}
