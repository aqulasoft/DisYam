package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.commands.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;



public class CommandManager {

    private final Map<String, Command> commands = new LinkedHashMap<>();


    public CommandManager() {
        addCommand(new SearchCommand());
        addCommand(new SearchTrackCommand());
        addCommand(new SearchPlaylistCommand());
        addCommand(new SearchArtistCommand());

        addCommand(new PlayServerPlaylistCommand());
        addCommand(new PlayCommand());
        addCommand(new StopCommand());
        addCommand(new PauseCommand());
        addCommand(new ResumeCommand());

        addCommand(new NextCommand());
        addCommand(new PrevCommand());

        addCommand(new SettingsCommand());
        addCommand(new DownloadCommand());
        addCommand(new RecommendationCommand());
        addCommand(new LyricsCommand());
        addCommand(new QueueInfoCommand());
    }

    private void addCommand(Command command) {
        if (!commands.containsKey(command.getInvoke())) {
            commands.put(command.getInvoke(), command);
        }
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }

    public Command getCommand(@NotNull String name) {
        return commands.get(name);
    }

    public void handleCommand(GuildMessageReceivedEvent event) {

        final String[] split = event.getMessage().getContentRaw().replaceFirst(
                "(?i)" + Pattern.quote(SettingsManager.get(event.getGuild().getName()).get("prefix")), "").split("\\s+");
        final String invoke = split[0].toLowerCase();

        if (invoke.equals("help")) {
            showHelp(event.getChannel(),SettingsManager.get(event.getGuild().getName()).get("prefix"));
            return;
        }

        if (commands.containsKey(invoke)) {
            final List<String> args = Arrays.asList(split).subList(1, split.length);
            commands.get(invoke).handle(args, event);
        }
    }

    private void showHelp(TextChannel channel,String prefix) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.addField("developed by aqulasoft.com", "https://github.com/aqulasoft/DisYam", false);
        commands.keySet().forEach(cmd -> {
            builder.addField(commands.get(cmd).getInvoke(), commands.get(cmd).getHelp(prefix), false);
        });
        channel.sendMessage(builder.build()).queue();
    }
}
