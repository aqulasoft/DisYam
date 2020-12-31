package com.aqulasoft.disyam;

import com.aqulasoft.disyam.audio.GuildMusicManager;
import com.aqulasoft.disyam.audio.PlayerManager;
import com.aqulasoft.disyam.commands.CommandManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static com.aqulasoft.disyam.utils.Consts.PREFIX;

class MessageListener extends ListenerAdapter {

    private final CommandManager manager;
    private final Logger log = LoggerFactory.getLogger(MessageListener.class);

    MessageListener(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public void onReady(ReadyEvent event) {
        log.info(String.format("Logged in as %#s", event.getJDA().getSelfUser()));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        User author = event.getAuthor();
        Message message = event.getMessage();
        String content = message.getContentDisplay();

        if (event.isFromType(ChannelType.TEXT)) {

            Guild guild = event.getGuild();
            TextChannel textChannel = event.getTextChannel();

            log.info(String.format("(%s)[%s]<%#s>: %s", guild.getName(), textChannel.getName(), author, content));
        } else if (event.isFromType(ChannelType.PRIVATE)) {
            log.info(String.format("[PRIV]<%#s>: %s", author, content));
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String rw = event.getMessage().getContentRaw();

        if (rw.equalsIgnoreCase(PREFIX + "shutdown")) {
            shutdown(event.getJDA());
            return;
        }

        if (rw.equalsIgnoreCase("!song")) {
            TextChannel channel = event.getChannel();
            PlayerManager playerManager = PlayerManager.getInstance();
            GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
            BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();

            if (queue.isEmpty()) {
                channel.sendMessage("The queue is empty").queue();

                return;
            }

            int trackCount = Math.min(queue.size(), 20);
            List<AudioTrack> tracks = new ArrayList<>(queue);
//            EmbedBuilder builder = EmbedUtils.defaultEmbed()
//                    .setTitle("Current Queue (Total: " + queue.size() + ")");

            for (int i = 0; i < trackCount; i++) {
                AudioTrack track = tracks.get(i);
                AudioTrackInfo info = track.getInfo();

//                builder.appendDescription(String.format(
//                        "%s - %s\n",
//                        info.title,
//                        info.author
//                ));
            }

            channel.sendMessage("builder.build()").queue();
        }


//        String prefix = "!"; //Constants.PREFIXES.computeIfAbsent(event.getGuild().getIdLong(), (l) -> Constants.PREFIX);

        if (!event.getAuthor().isBot() && !event.getMessage().isWebhookMessage() && rw.startsWith(PREFIX)) {
            manager.handleCommand(event);
            log.info("HANDLE");
        }
    }

    private void shutdown(JDA jda) {
        jda.shutdown();
        System.exit(0);
    }
}