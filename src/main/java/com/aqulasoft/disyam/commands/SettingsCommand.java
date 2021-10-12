package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlayerState;
import com.aqulasoft.disyam.models.bot.SettingsState;
import com.aqulasoft.disyam.service.BotStateManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class SettingsCommand implements Command {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.appendDescription("please wait");
        builder.setColor(Color.ORANGE);
        event.getChannel().sendMessage(builder.build()).queue(message -> {
            PlayerState state = BotStateManager.getInstance().getPlayerState(event.getGuild().getIdLong());
            SettingsState settingsState = new SettingsState(message, event.getGuild(),state);
            BotStateManager.getInstance().setState(event.getGuild().getIdLong(), settingsState, false);
            settingsState.updateMessage(true, false, null);
        });
    }

    @Override
    public String getHelp(String prefix) {
        return "Shows your bot server settings\n" +
                "Usage: `" + prefix + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "settings";
    }
}
