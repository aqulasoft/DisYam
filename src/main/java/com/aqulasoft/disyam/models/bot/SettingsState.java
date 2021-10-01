package com.aqulasoft.disyam.models.bot;

import Db.DbManager;
import com.aqulasoft.disyam.utils.BotStateType;
import com.aqulasoft.disyam.utils.SettingsStateType;
import lombok.Getter;
import models.SettingsDao;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.aqulasoft.disyam.utils.Consts.*;

public class SettingsState implements BotState {
    @Getter
    private Message message;
    @Getter
    private final Guild guild;
    SettingsStateType state  = SettingsStateType.SETTINGS_STATE_TYPE;

    public SettingsState(Message message, Guild guild) {
        this.message = message;
        this.guild = guild;

    }

    public void updateMessage(boolean addReactions) {
        message.editMessage(buildMessage(addReactions)).queue(m -> {
            message = m;
        });
    }

    private MessageEmbed buildMessage(boolean addReactions) {
        EmbedBuilder builder = new EmbedBuilder();
        DbManager dbManager = DbManager.getInstance();
        Map<String, String> daoMap = new HashMap<>();
        message.getEmbeds().get(0).getTitle();
        builder.setTitle("Settings");
        builder.setColor(Color.ORANGE);
        SettingsDao info = dbManager.getSettingsInfo(guild.getName());
        if ( info == null){
            builder.setDescription("you don't have bot settings yet");
        } else {
            if (!info.getPrefix().equals("") & info.getValueOfVolume() != null){
                daoMap.put("prefix", info.getPrefix());
                daoMap.put("volume", String.valueOf(info.getValueOfVolume()));
                String mapAsString = daoMap.keySet().stream()
                        .map(key -> key + "=" + daoMap.get(key))
                        .collect(Collectors.joining(", ", "{", "}"));
                builder.setDescription(mapAsString);
            }else if (!info.getPrefix().equals("") & info.getValueOfVolume() == null){
                builder.setDescription("prefix" + info.getPrefix());
            }
            else {
                builder.setDescription("volume" + (info.getValueOfVolume()));
            }

        }
        if (addReactions) {
            message.addReaction(EMOJI_PREFIX).queue();
            message.addReaction(EMOJI_VOLUME).queue();
        }
        return builder.build();
    }

    @Override
    public void setMessage(Message msg) {

    }

    @Override
    public BotStateType getType() {
        return null;
    }
}
