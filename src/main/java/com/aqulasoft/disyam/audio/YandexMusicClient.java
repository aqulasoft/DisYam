package com.aqulasoft.disyam.audio;


import com.aqulasoft.disyam.models.audio.*;
import com.aqulasoft.disyam.models.bot.ChannelPlaylistName;
import com.aqulasoft.disyam.models.bot.Operation;
import com.aqulasoft.disyam.models.dto.UserPlaylistDto;
import com.aqulasoft.disyam.models.dto.YaResponseDto;
import com.aqulasoft.disyam.service.PlaylistManager;
import com.aqulasoft.disyam.service.SecretManager;
//import kong.unirest.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.*;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.aqulasoft.disyam.utils.Consts.*;

public class YandexMusicClient {


    static Logger log = Logger.getLogger(YandexMusicClient.class);


    public static String getTrackDownloadLink(long songId) {
//        log.info(String.format("%s/tracks/%s/download-info", baseUrl, songId));
        GetRequest request = Unirest.get(String.format("%s/tracks/%s/download-info", baseUrl, songId)).header("Authorization", "OAuth " + SecretManager.get("YaToken"));
        String res = request.asJson().getBody().getObject().getJSONArray("result").getJSONObject(0).getString("downloadInfoUrl");
        byte[] body = Unirest.get(res).header("Authorization", "OAuth " + SecretManager.get("YaToken")).asBytes().getBody();
        DownloadInfo dInfo = DownloadInfo.create(body);
        if (dInfo != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(String.format("XGRlBW9FXlekgbPrRHuSiA%s%s", dInfo.getPath().substring(1), dInfo.getS()).getBytes(StandardCharsets.UTF_8));
                byte[] digest = md.digest();
                BigInteger bigInt = new BigInteger(1, digest);
                StringBuilder sign = new StringBuilder(bigInt.toString(16));

                while (sign.length() < 32) {
                    sign.insert(0, "0");
                }

                return String.format("https://%s/get-mp3/%s/%s%s", dInfo.getHost(), sign.toString(), dInfo.getTs(), dInfo.getPath());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static MultipartBody getAuthRequest(String username, String password) {
// Json
        return Unirest.post(authUrl + "/token")
                .field("grant_type", "password")
                .field("client_id", CLIENT_ID)
                .field("client_secret", CLIENT_SECRET)
                .field("username", username)
                .field("password", password);
    }

    public static int enterCaptcha(String captchaKey, String username, String password) {
        String answer = "";
        try (InputStreamReader is = new InputStreamReader(System.in)) {
            try (BufferedReader reader = new BufferedReader(is)) {
                System.out.println("Input captcha answer");
                answer = reader.readLine();
            }
            MultipartBody request = getAuthRequest(username, password);
            request.field("x_captcha_answer", answer);
            request.field("x_captcha_key", captchaKey);
            return request.asString().getStatus();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static YaSearchResult search(String searchStr, String type, int page, int pageSize) {
        String searchUrl = baseUrl + "/search";
        GetRequest request = Unirest.get(searchUrl)
                .queryString("text", searchStr)
                .queryString("type", type)
                .queryString("nocorrect", false)
                .queryString("page", page)
                .queryString("page-size", pageSize);
        return new YaSearchResult(request.asJson().getBody().getObject().getJSONObject("result"), type);
    }

    public static byte[] downloadSong(long songId) {
        String token = SecretManager.get("YaToken");
        String link = getTrackDownloadLink(songId);
        return Unirest.get(link).header("Authorization", "OAuth " + token).asBytes().getBody();
    }

    public static YaPlaylist getPlaylist(String username, String playlistId) {
        Unirest.config().reset().enableCookieManagement(false);
        String url = String.format("https://music.yandex.ru/handlers/playlist.jsx?owner=%s&kinds=%s&light=true&madeFor=&lang=%s&external-domain=music.yandex.ru&overembed=false&ncrnd=0.9083773647705418", username, playlistId, "ru");
        JsonNode body = Unirest.get(url).asJson().getBody();
        Unirest.config().reset().enableCookieManagement(true);
        return new YaPlaylist(body.getObject().getJSONObject("playlist"));

    }

    public static List<UserPlaylistDto> getUserPlaylist(int kinds){
        String url = String.format("%s/users/%s/playlists", baseUrl, SecretManager.get("username"));
        return Unirest.get(url)
                .header("Authorization", "OAuth " + SecretManager.get("YaToken"))
                .queryString("kinds", kinds).asObject(new GenericType<YaResponseDto<List<UserPlaylistDto>>>() {
                }).getBody().getResult();


    }


    public static String createPlaylist(String name) {
        ChannelPlaylistName ChannelName = new ChannelPlaylistName();
        if (!ChannelName.getState()) {
            String url = String.format("%s/users/%s/playlists/create", baseUrl, SecretManager.get("username"));
            Unirest.post(url).field("title", name)
                    .field("visibility", "public")
                    .header("Authorization", "OAuth " + SecretManager.get("YaToken"))
                    .asJson().getBody().toPrettyString();
        }
        return name;
    }
    public static void addTrackToPlaylist(int kind, long trackId, int albumId, int revision) throws PlaylistWrongRevisionException {
        Operation operation = new Operation(0,trackId, albumId);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        String difference;
        try {
            difference = mapper.writeValueAsString(operation);
        } catch (JsonProcessingException e) {
            log.error(e.getLocalizedMessage());
            return;
        }
        String url = String.format("%s/users/%s/playlists/%s/change", baseUrl, SecretManager.get("username"), kind);
        String diff = String.format("[%s]",difference);
        String result;
        System.out.println(
        result = Unirest.post(url)
                .header("Authorization", "OAuth " + SecretManager.get("YaToken"))
                .field("revision", revision)
                .field("diff", String.valueOf(diff))
                .asJson().getBody().toString());
        if (result.contains("wrong-revision")){
            throw new PlaylistWrongRevisionException("Unable to get revision");
        }

    }


    public static List<UserPlaylistDto> getUserPlaylists(){
        String url = String.format("%s/users/%s/playlists/list", baseUrl, SecretManager.get("username"));
        return Unirest.get(url)
                .header("Authorization", "OAuth " + SecretManager.get("YaToken"))
                .asObject(new GenericType<YaResponseDto<List<UserPlaylistDto>>>() {
                }).getBody().getResult();

    }


    public static YaPlaylist getArtistTracks(YaArtist artist) {
        String url = String.format("%s/artists/%s/tracks", baseUrl, artist.getId());
        JsonNode body = Unirest.get(url).queryString("page-size", 100).asJson().getBody();
        return YaPlaylist.createArtistPlaylist(body.getObject().getJSONObject("result"), artist);
    }

    public static String getAccountInfo() {
        return Unirest.get(String.format("%s/rotor/account/status", baseUrl)).queryString("user", "1292461505")
                .header("Authorization", "OAuth " + SecretManager.get("YaToken"))
                .asJson().getBody().toPrettyString();
    }

    public static String getPlaylistRecommendations(long userId, long playlistId) {
        String url = String.format("%s/users/%d/playlists/%d/recommendations", baseUrl, userId, playlistId);
        return Unirest.get(url)
                .header("Authorization", "OAuth " + SecretManager.get("YaToken"))
                .asJson().getBody().toPrettyString();
    }

    public static YaStationSequence getStationTracks(YaTrack track, Long trackId) {
        String url = String.format("%s/rotor/station/%s/tracks", baseUrl, String.format("track:%s", track.getId()));
        return new YaStationSequence(Unirest.get(url)
                .queryString("settings2", false)
                .queryString("queue", trackId)
                .header("Authorization", "OAuth " + SecretManager.get("YaToken"))
                .asJson().getBody().getObject().getJSONObject("result"), track);
    }

    public static void sendStationFeedback(String station, String type, String batchId, Long trackId, Long position) {
        String url = String.format("%s/rotor/station/%s/feedback", baseUrl, station);
        JsonNode jsonNode = new JsonNode("{}");
        jsonNode.getObject().put("type", type);
        jsonNode.getObject().put("timestamp", new Date().getTime());
        if (position != null) jsonNode.getObject().put("totalPlayedSeconds", position);
        jsonNode.getObject().put("batch-id", batchId);
        if (trackId != null) jsonNode.getObject().put("trackId", trackId);

        RequestBodyEntity request = Unirest.post(url)
                .header("Authorization", "OAuth " + SecretManager.get("YaToken"))
                .body(jsonNode);

        request.asJson();
    }

    public static void playAudio(YaTrack track) {
        String url = String.format("%s/play-audio", baseUrl);

        HashMap<String, Object> map = new HashMap<>();
        map.put("track-id", track.getId());
        map.put("from", SecretManager.get("uid"));
        map.put("album-id", track.getAlbums().get(0).getId());
        map.put("timestamp", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
        map.put("play-id", "");

        Unirest.post(url)
                .header("Authorization", "OAuth " + SecretManager.get("YaToken"))
                .fields(map).asEmpty();
    }

    public static YaTrackSupplement getTrackSupplement(long trackId) {
        String url = String.format("%s/tracks/%s/supplement", baseUrl, trackId);
        HttpResponse<JsonNode> res = Unirest.get(url).asJson();
        return new YaTrackSupplement(res.getBody().getObject().getJSONObject("result"));
    }
}

