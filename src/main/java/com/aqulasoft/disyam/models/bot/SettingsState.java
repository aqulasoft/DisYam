package com.aqulasoft.disyam.models.bot;

import com.aqulasoft.disyam.Db.DbManager;
import com.aqulasoft.disyam.utils.BotStateType;
import com.aqulasoft.disyam.utils.SettingsStateType;
import lombok.Getter;
import com.aqulasoft.disyam.Db.models.SettingsDao;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aqulasoft.disyam.utils.Consts.*;

public class SettingsState implements BotState {
    @Getter
    private Message message;
    @Getter
    private final Guild guild;
    @Getter
    public SettingsStateType stateType;

    public SettingsState(Message message, Guild guild) {
        this.message = message;
        this.guild = guild;

    }

    @Override
    public void setMessage(Message msg) {

    }

    @Override
    public BotStateType getType() {
        return null;
    }

    public void setSettingsType(String value) {
        if (value == null) {
            stateType = null;
            return;
        }

        if (value.equals("prefix")) {
            stateType = SettingsStateType.PREFIX_STATE_TYPE;
        } else if (value.equals("volume")) {
            stateType = SettingsStateType.VOLUME_STATE_TYPE;
        }

    }


    public void updateMessage(boolean addReactions, Boolean bool, String name) {
        if (!bool) {
            message.editMessage(buildMessage(addReactions)).queue(m -> {
                message = m;
            });
        } else {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.ORANGE);
            SettingsDao settingsDao = DbManager.getInstance().getSettingsInfo(guild.getName());
            builder.setTitle("Settings");

            if (settingsDao == null || name != null) {
                if (name.equals("volumeException")){
                    builder.setDescription("**PLease, enter value of volume in range { 0 , 200 }**");
                }else if (name.equals("prefixException")){
                    builder.setDescription("**Sorry, but the prefix must be one character**");
                }
                else {
                    if(name.equals("prefix")){
                        builder.setDescription(String.format("**%s Please,enter new value of %s**",EMOJI_PREFIX, name));
                    }else if (name.equals("volume")){
                        builder.setDescription(String.format("**%s Please,enter new value of %s**",EMOJI_VOLUME, name));
                    }
                }
            } else {
                if (settingsDao.getPrefix() != null & settingsDao.getValueOfVolume() != null) {
                    builder.setDescription(String.format("**%s Prefix: %s\n%s Volume: %s**",EMOJI_PREFIX, settingsDao.getPrefix(),EMOJI_VOLUME, settingsDao.getValueOfVolume()));
                } else if (settingsDao.getPrefix() != null) {
                    builder.setDescription(String.format("**%s Prefix: %s**",EMOJI_PREFIX, settingsDao.getPrefix()));
                } else if (settingsDao.getValueOfVolume() != null) {
                    builder.setDescription(String.format("**%s Volume: %s**",EMOJI_VOLUME, settingsDao.getValueOfVolume()));
                }
            }
            message.editMessage(builder.build()).queue();

        }
    }

    private MessageEmbed buildMessage(boolean addReactions) {
        EmbedBuilder builder = new EmbedBuilder();
        DbManager dbManager = DbManager.getInstance();
        message.getEmbeds().get(0).getTitle();
        builder.setTitle("Settings");
        builder.setColor(Color.ORANGE);
        SettingsDao settingsDao = dbManager.getSettingsInfo(guild.getName());
        if (settingsDao != null) {
            if (!settingsDao.getPrefix().equals("") & settingsDao.getValueOfVolume() != null) {
                builder.setDescription(String.format("**%s Prefix:%s** \n**%s Volume: %s**",EMOJI_PREFIX, settingsDao.getPrefix(),EMOJI_VOLUME, settingsDao.getValueOfVolume()));
            } else if (!settingsDao.getPrefix().equals("") & settingsDao.getValueOfVolume() == null) {
                builder.setDescription(String.format("%s Prefix: %s",EMOJI_PREFIX ,settingsDao.getPrefix()));
            } else {
                builder.setDescription(String.format("%s Volume: %s ",EMOJI_VOLUME,settingsDao.getValueOfVolume()));
            }
        } else {
            builder.setDescription("**You don't have bot settings yet**");
        }
        if (addReactions) {
            message.addReaction(EMOJI_PREFIX).queue();
            message.addReaction(EMOJI_VOLUME).queue();
        }
        return builder.build();
    }
}