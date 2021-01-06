package com.aqulasoft.disyam;

import com.aqulasoft.disyam.service.CommandManager;
import com.aqulasoft.disyam.service.MessageListener;
import com.aqulasoft.disyam.service.SecretManager;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.MultipartBody;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.log4j.Logger;

import javax.security.auth.login.LoginException;

import static com.aqulasoft.disyam.audio.YandexMusicClient.enterCaptcha;
import static com.aqulasoft.disyam.audio.YandexMusicClient.getAuthRequest;


public class DisYamBot {
    private String YaToken;
    private final String botToken;

    static Logger log = Logger.getLogger(DisYamBot.class);

    public DisYamBot(String botToken, String username, String password) {
        this.botToken = botToken;
        MultipartBody request = getAuthRequest(username, password);
        HttpResponse<JsonNode> res = request.asJson();
        if (res.getStatus() == 200) {
            YaToken = res.getBody().getObject().getString("access_token");
            log.info("Got Yandex Auth token: " + YaToken);
            SecretManager.set("YaToken", YaToken);
        } else {
            String capchaKey = res.getBody().getObject().getString("x_captcha_key");
            if (capchaKey != null) {
                enterCaptcha(capchaKey, username, password);
                YaToken = res.getBody().getObject().getString("access_token");
                log.info("Got Ya token: " + YaToken);
                SecretManager.set("YaToken", YaToken);
            }
        }

    }

    public void Start() {
        CommandManager commandManager = new CommandManager();
        MessageListener messageListener = new MessageListener(commandManager);

        try {
            log.info("Booting");
            JDABuilder builder = JDABuilder.createDefault(botToken);
            builder.addEventListeners(messageListener);
            builder.setActivity(Activity.playing("Yandex Music"));
            builder.build().awaitReady();
            log.info("Running");
        } catch (LoginException | InterruptedException e) {
            log.error(e);
        }
    }
}
