package com.aqulasoft.disyam.utils;

import com.aqulasoft.disyam.models.DownloadInfo;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.aqulasoft.disyam.utils.Consts.baseUrl;

public class Utils {

    public static String getTrackDownloadLink(String token, long songId) {
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
}
