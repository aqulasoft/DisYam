package com.aqulasoft.disyam.models.bot;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

import static com.aqulasoft.disyam.utils.Consts.*;

public class SettingsState {
    @Getter
    private Message message;
    @Getter
    private final Guild guild;

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

        message.getEmbeds().get(0).getTitle();
        builder.setTitle("Settings");
        builder.setColor(Color.ORANGE);
        if (addReactions) {

            message.addReaction(EMOJI_PREFIX).queue();
            message.addReaction(EMOJI_VOLUME).queue();
        }
        return builder.build();
    }
}
