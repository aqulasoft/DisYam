package com.aqulasoft.disyam.utils;

import java.util.HashMap;
import java.util.Map;

public class Consts {
    public static final String baseUrl = "https://api.music.yandex.net";
    public static final String authUrl = "https://oauth.yandex.ru";
    public static final String CLIENT_ID = "23cabbbdc6cd418abb4b39c32c41195d";
    public static final String CLIENT_SECRET = "53bc75238f0c4d08a118e51fe9203300";
    public static final String PLAYLIST_URL_REGEX = ".*music\\.yandex\\.ru/users/(.+)/playlists/(\\d+)\\D*";

    public static final int INACTIVITY_MINUTE_MAX = 5;
    public static final int INACTIVITY_CHECK_PERIOD = 60000;

    public static final String EMOJI_SHUFFLE = "\uD83D\uDD00";
    public static final String EMOJI_PREVIOUS = "⏮️";
    public static final String EMOJI_NEXT = "⏭️";
    public static final String EMOJI_PLAY_PAUSE = "⏯️";
    public static final String EMOJI_REPEAT_ONE = "\uD83D\uDD02";
    public static final String EMOJI_DOWNLOAD = "\uD83D\uDCE5";
    public static final String EMOJI_STOP = "\uD83D\uDEAB";
    public static final String EMOJI_CANCEL = "\uD83D\uDEAB";
    public static final String EMOJI_OK = "✔";
    public static final String EMOJI_LIKE = "\uD83D\uDD25";
    public static final String EMOJI_DISLIKE = "❌";
    public static final String EMOJI_PREFIX = "\uD83C\uDD7F";
    public static final String EMOJI_VOLUME = "\uD83D\uDD08";
    public static final String EMOJI_DONE = "✅";
    public static final String EMOJI_STATUS = "\uD83D\uDD51";

    public static final Map<String, String> EMOJI_MAP = new HashMap() {{
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
