package com.aqulasoft.disyam.utils;

import com.aqulasoft.disyam.DisYamBot;
import com.aqulasoft.disyam.models.DownloadInfo;
import kong.unirest.GetRequest;
import kong.unirest.MultipartBody;
import kong.unirest.Unirest;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.aqulasoft.disyam.utils.Consts.*;
import static com.aqulasoft.disyam.utils.Consts.CLIENT_SECRET;

public class Utils {
    static Logger log = Logger.getLogger(Utils.class);

    public static String getTrackDownloadLink(String token, long songId) {
        log.info(token);
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

    public static byte[] downloadSong(String token, long songId) {
        String link = getTrackDownloadLink(token, songId);

        return Unirest.get(link).header("Authorization", "OAuth " + token).asBytes().getBody();
    }
}
