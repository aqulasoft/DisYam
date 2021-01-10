package com.aqulasoft.disyam.service;

import com.aqulasoft.disyam.commands.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

import static com.aqulasoft.disyam.utils.Consts.PREFIX;

public class CommandManager {

    private final Map<String, Command> commands = new LinkedHashMap<>();

    public CommandManager() {
        addCommand(new PlayCommand());
        addCommand(new StopCommand());
        addCommand(new PauseCommand());
        addCommand(new ResumeCommand());

        addCommand(new NextCommand());
        addCommand(new PrevCommand());

        addCommand(new DownloadCommand());
        addCommand(new RecommendationCommand());

        addCommand(new SearchCommand());
        addCommand(new SearchTrackCommand());
        addCommand(new SearchPlaylistCommand());
        addCommand(new SearchArtistCommand());
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
                "(?i)" + Pattern.quote(PREFIX), "").split("\\s+");
        final String invoke = split[0].toLowerCase();

        if (invoke.equals("help")) {
            showHelp(event.getChannel());
            return;
        }

        if (commands.containsKey(invoke)) {
            final List<String> args = Arrays.asList(split).subList(1, split.length);
            commands.get(invoke).handle(args, event);
        }
    }

    private void showHelp(TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();

        commands.keySet().forEach(cmd -> {
            builder.addField(commands.get(cmd).getInvoke(), commands.get(cmd).getHelp(), false);
        });
        channel.sendMessage(builder.build()).queue();
    }
}