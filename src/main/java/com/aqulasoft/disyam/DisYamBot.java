package com.aqulasoft.disyam;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import kong.unirest.*;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

import static com.aqulasoft.disyam.utils.Consts.*;
import static com.aqulasoft.disyam.utils.Utils.getTrackDownloadLink;

public class DisYamBot {


    public static void main(final String[] args) {
        final String token = args[0];
        final String username = args[1];
        final String password = args[2];
        final DiscordClient client = DiscordClient.create(token);
        final GatewayDiscordClient gateway = client.login().block();
        AtomicReference<String> Ytoken = new AtomicReference<>();
        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            String msg = message.getContent();
            if (msg.length() > 0 && !event.getMessage().getAuthor().get().isBot()) {
                final MessageChannel channel = message.getChannel().block();

                if (msg.equals("!auth")) {
                    MultipartBody request = getAuthRequest(username, password);
                    HttpResponse<JsonNode> res = request.asJson();
                    System.out.println(res.getBody());
                    if (res.getStatus() == 403) {
                        String capchaKey = res.getBody().getObject().getString("x_captcha_key");
                        channel.createMessage(enterCapcha(capchaKey, username, password)).block();
                    }
                    Ytoken.set(res.getBody().getObject().getString("access_token"));
                    channel.createMessage(res.getBody().toPrettyString()).block();
                }

                if (msg.contains("!song")) {
                    try {
                        System.out.println(msg.substring(5));
                        byte[] song = downloadSong(Ytoken.get(), Long.parseLong(msg.substring(5).trim()));

                        if (song != null) {

                            Mono<TextChannel> textChannel = getTextChannel(gateway, channel.getId());
                            textChannel
                                    .flatMap(c -> c.createMessage(a -> a.addFile("song.mp3", new ByteArrayInputStream(song)))).block();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                if (msg.contains("!search")) {
                    channel.createMessage(search(msg)).block();
                }

            }

        });

        gateway.onDisconnect().block();
    }

    private static Mono<TextChannel> getTextChannel(GatewayDiscordClient discordClient, Snowflake channel) {
        return discordClient.getChannelById(channel)
                .map(TextChannel.class::cast);
    }

    private static byte[] downloadSong(String token, long songId) {
        String link = getTrackDownloadLink(token, songId);

        return Unirest.get(link).header("Authorization", "OAuth " + token).asBytes().getBody();
    }

    private static MultipartBody getAuthRequest(String username, String password) {
        return Unirest.post(authUrl + "/token")
                .field("grant_type", "password")
                .field("client_id", CLIENT_ID)
                .field("client_secret", CLIENT_SECRET)
                .field("username", username)
                .field("password", password);
    }

    private static String enterCapcha(String captchaKey, String username, String password) {
        String answer = "";
        try (InputStreamReader is = new InputStreamReader(System.in)) {
            try (BufferedReader reader = new BufferedReader(is)) {
                System.out.println("Input capcha answer");
                answer = reader.readLine();
            }
            MultipartBody request = getAuthRequest(username, password);
            request.field("x_captcha_answer", answer);
            request.field("x_captcha_key", captchaKey);
            String res = request.asString().getBody();
            System.out.println(res);
            return res;
        } catch (Exception e) {
            System.out.println("ERRRO");
        }
        return answer;
    }

    private static String search(String searchStr) {
        String searchUrl = baseUrl + "/search";
        GetRequest request = Unirest.get(searchUrl)
                .queryString("text", searchStr.substring(8))
                .queryString("type", "track")
                .queryString("nocorrect", false)
                .queryString("page", 0);

        return request.asJson().getBody().toPrettyString().substring(0, 1500) + "....";
    }
}
