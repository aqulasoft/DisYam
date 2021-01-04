package com.aqulasoft.disyam.audio;

import com.aqulasoft.disyam.models.audio.DownloadInfo;
import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.models.audio.YaSearchResult;
import com.aqulasoft.disyam.service.SecretManager;
import com.aqulasoft.disyam.utils.Utils;
import kong.unirest.*;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.aqulasoft.disyam.utils.Consts.*;

public class YandexMusicManager {

    static Logger log = Logger.getLogger(Utils.class);

    public static String getTrackDownloadLink(String token, long songId) {
        log.info(String.format("%s/tracks/%s/download-info", baseUrl, songId));
        GetRequest request = Unirest.get(String.format("%s/tracks/%s/download-info", baseUrl, songId)).header("Authorization", "OAuth " + token);
        String res = request.asJson().getBody().getObject().getJSONArray("result").getJSONObject(0).getString("downloadInfoUrl");
        byte[] body = Unirest.get(res).header("Authorization", "OAuth " + token).asBytes().getBody();
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

    public static YaSearchResult search(String searchStr, String type) {
        String searchUrl = baseUrl + "/search";
        GetRequest request = Unirest.get(searchUrl)
                .queryString("text", searchStr)
                .queryString("type", type)
                .queryString("nocorrect", false)
                .queryString("page", 0);
        return YaSearchResult.create(request.asJson().getBody().getObject().getJSONObject("result"));
    }

    public static byte[] downloadSong(long songId) {
        String token = SecretManager.get("YaToken");
        String link = getTrackDownloadLink(token, songId);
        return Unirest.get(link).header("Authorization", "OAuth " + token).asBytes().getBody();
    }

    public static YaPlaylist getPlaylist(String username, String playlistId) {
        Unirest.config().reset().enableCookieManagement(false);
        String url = String.format("https://music.yandex.ru/handlers/playlist.jsx?owner=%s&kinds=%s&light=true&madeFor=&lang=%s&external-domain=music.yandex.ru&overembed=false&ncrnd=0.9083773647705418", username, playlistId, "ru");
        GetRequest request = Unirest.get(url);
        HttpResponse<JsonNode> json = request.asJson();
        JsonNode body = json.getBody();
        Unirest.config().reset().enableCookieManagement(true);
        return YaPlaylist.create(body.getObject().getJSONObject("playlist"));
    }


}
