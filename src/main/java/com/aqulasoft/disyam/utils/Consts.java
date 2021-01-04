package com.aqulasoft.disyam.utils;

import java.util.HashMap;
import java.util.Map;

public class Consts {

    public static String baseUrl = "https://api.music.yandex.net";
    public static String authUrl = "https://oauth.yandex.ru";
    public static String CLIENT_ID = "23cabbbdc6cd418abb4b39c32c41195d";
    public static String CLIENT_SECRET = "53bc75238f0c4d08a118e51fe9203300";
    public static String PREFIX = "!";
    public static String PLAYLIST_URL_REGEX = ".*music\\.yandex\\.ru/users/(.+)/playlists/(\\d+)\\D*";

    public static Map<String, String> EMOJI_MAP = new HashMap() {{
        put("1", "1️⃣");
        put("2", "2️⃣");
        put("3", "3️⃣");
        put("4", "4️⃣");
        put("5", "5️⃣");
        put("6", "6️⃣");
        put("7", "7️⃣");
        put("8", "8️⃣");
        put("9", "9️⃣");
        put("10", "\uD83D\uDD1F");
    }};
}
