package com.aqulasoft.disyam.commands;

import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.models.audio.YaPlaylist;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.aqulasoft.disyam.models.bot.PlaylistState;
import com.aqulasoft.disyam.service.BotStateManager;
import com.aqulasoft.disyam.service.PlaylistManager;
import com.aqulasoft.disyam.service.SecretManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.List;

import static com.aqulasoft.disyam.audio.YandexMusicClient.getPlaylist;
import static com.aqulasoft.disyam.utils.Consts.PREFIX;
import static com.aqulasoft.disyam.utils.Utils.joinVoice;

public class PlayServerPlaylistCommand implements Command{
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        if (joinVoice(event, channel)) return;
        YaPlaylist playlist = getPlaylist(SecretManager.get("username"), PlaylistManager.getInstance().getKind(event.getGuild().getName()));
        if (playlist.getTracks().size() == 0){
            System.out.println("playlist is empty");
            event.getChannel().sendMessage("Please add some tracks to your server playlist");
            return;
        }
        YaTrack track = playlist.getTrack(0);
        if (track != null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.appendDescription("sus");
            builder.setColor(Color.ORANGE);
            event.getChannel().sendMessage(builder.build()).queue(message -> {
                PlaylistState state = new PlaylistState(playlist, message, event.getGuild());
                BotStateManager.getInstance().setState(event.getGuild().getIdLong(), state, false);
                state.updateMessage(true);
                PlayerManager playerManager = PlayerManager.getInstance();
                playerManager.loadAndPlayPlaylist(event.getChannel());
            });
        }
    }

    @Override
    public String getHelp() {
        return "Play your server playlist\n" +
                "Usage: `" + PREFIX + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "fav";
    }
}
