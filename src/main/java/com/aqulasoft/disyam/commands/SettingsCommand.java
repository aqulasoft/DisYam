package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.SettingsState;
import com.aqulasoft.disyam.service.BotStateManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

import static com.aqulasoft.disyam.utils.Consts.PREFIX;

public class SettingsCommand implements Command {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {

        EmbedBuilder builder = new EmbedBuilder();
        builder.appendDescription("please wait");
        builder.setColor(Color.ORANGE);
        BotState state = BotStateManager.getInstance().getState(event.getGuild().getIdLong());
        event.getChannel().sendMessage(builder.build()).queue(message -> {
            SettingsState settingsState = new SettingsState(message, event.getGuild());
            settingsState.updateMessage(true);
        });
    }

    @Override
    public String getHelp() {
        return "Shows your bot server settings\n" +
                "Usage: `" + PREFIX + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "settings";
    }
}
