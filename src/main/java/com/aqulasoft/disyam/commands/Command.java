package com.aqulasoft.disyam.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public interface Command {

    void handle(List<String> args, GuildMessageReceivedEvent event);

    String getHelp(String prefix);

    String getInvoke();
}
