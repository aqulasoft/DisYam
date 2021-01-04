package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.models.bot.BotState;
import com.aqulasoft.disyam.models.bot.PlaylistState;
import com.aqulasoft.disyam.service.BotStateManager;
import com.aqulasoft.disyam.utils.BotStateType;
import com.aqulasoft.disyam.utils.Utils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class ShuffleCommand implements Command {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {

        TextChannel channel = event.getChannel();
        if (!Utils.checkVoiceChannelAvailability(event, channel)) return;
        BotState state = BotStateManager.getInstance().getState(event.getGuild().getIdLong());
        if (state != null && state.getType() == BotStateType.YA_PLAYLIST) {
            ((PlaylistState) state).updateShuffle();
        }
        event.getMessage().delete().queue();
    }

    @Override
    public String getHelp() {
        return "Shuffle songs";
    }

    @Override
    public String getInvoke() {
        return "shuffle";
    }
}
