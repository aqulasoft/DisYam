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

import static com.aqulasoft.disyam.utils.Consts.*;

public class SettingsState implements BotState {
    @Getter
    private Message message;
    @Getter
    private final Guild guild;
    @Getter
    public SettingsStateType stateType;
    @Getter
    public PlayerState state;

    public SettingsState(Message message, Guild guild, PlayerState state) {
        this.message = message;
        this.guild = guild;
        this.state = state;
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
        } else if (value.equals("status")) {
            stateType = SettingsStateType.STATUS_STATE_TYPE;
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
            SettingsDao settingsDao = DbManager.getInstance().getSettingsInfo(guild.getIdLong());
            builder.setTitle("Settings");

            if (settingsDao == null || name != null) {
                switch (name) {
                    case "volumeException":
                        builder.setDescription("**Please, enter value of volume in range 0 100**");
                        break;
                    case "prefixException":
                        builder.setDescription("**Sorry, but the prefix must be one character**");
                        break;
                    case "statusTypeException":
                        builder.setDescription("**Please, enter on or off**");
                        break;
                    default:
                        switch (name) {
                            case "prefix":
                                builder.setDescription(String.format("**%s Please,enter new value of %s**", EMOJI_PREFIX, name));
                                break;
                            case "volume":
                                builder.setDescription(String.format("**%s Please,enter new value of %s**", EMOJI_VOLUME, name));
                                break;
                            case "status":
                                builder.setDescription(String.format("**%s Please,enter on or off track status**", EMOJI_STATUS));
                                break;
                        }
                        break;
                }
            } else {
                String status;
                if (settingsDao.getShowTrackProgress() != null) {
                    if (settingsDao.getShowTrackProgress()) {
                        status = "on";
                    } else{
                        status = "off";
                    }
                }else status = null;
                builder.setDescription(String.format("**%s Volume: %s\n%s Prefix: %s\n%s ShowTrackStatus: %s**", EMOJI_PREFIX, settingsDao.getPrefix(), EMOJI_VOLUME, settingsDao.getValueOfVolume(), EMOJI_STATUS, status));
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
        SettingsDao settingsDao = dbManager.getSettingsInfo(guild.getIdLong());
        if (settingsDao != null) {
            String status;
            if (settingsDao.getShowTrackProgress() != null) {
                if (settingsDao.getShowTrackProgress()) {
                    status = "on";
                } else {
                    status = "off";
                }
            } else status = null;
            builder.setDescription(String.format("**%s Prefix: %s\n%s Volume: %s\n%s ShowTrackStatus: %s**", EMOJI_PREFIX, settingsDao.getPrefix(), EMOJI_VOLUME, settingsDao.getValueOfVolume(), EMOJI_STATUS, status));
        } else {
            builder.setDescription("**You don't have bot settings yet**");
        }
        if (addReactions) {
            message.addReaction(EMOJI_PREFIX).queue();
            message.addReaction(EMOJI_VOLUME).queue();
            message.addReaction(EMOJI_STATUS).queue();
            message.addReaction(EMOJI_DONE).queue();
        }
        return builder.build();
    }
}