package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.service.SecretManager;
import com.aqulasoft.disyam.utils.Utils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.aqulasoft.disyam.utils.Consts.PREFIX;

public class PlayCommand implements Command {

    public PlayCommand() {

    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {

        TextChannel channel = event.getChannel();

        if (args.isEmpty()) {
            channel.sendMessage("Please provide some arguments").queue();
            return;
        }
        String input = Utils.getTrackDownloadLink(SecretManager.get("YaToken"), Long.parseLong(args.get(0)));

        PlayerManager manager = PlayerManager.getInstance();

        manager.loadAndPlay(event.getChannel(), input);
    }

    private boolean isUrl(String input) {
        try {
            new URL(input);

            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }


    @Override
    public String getHelp() {
        return "Plays a song\n" +
                "Usage: `" + PREFIX + getInvoke() + " <song url>`";
    }

    @Override
    public String getInvoke() {
        return "play";
    }
}
