package com.aqulasoft.disyam.models.dto;

import com.aqulasoft.disyam.models.audio.YaArtist;
import com.aqulasoft.disyam.models.audio.YaTrack;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter

public class YaPlaylistDto {

    private long id;
    private String title;
    private YaArtist owner;
    private long duration;
    private int trackCount;
    private String description;
    private List<YaTrack> tracks;
    private String descriptionFormatted;
    private Guild guild;

}
