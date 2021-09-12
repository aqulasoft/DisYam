package com.aqulasoft.disyam.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPlaylistDto {

    private PlaylistOwnerDto owner;
    private String playlistUuid;
    private Boolean available;
    private int uid;
    private int kind;
    private String title;
    private int revision;
    private int snapshot;
    private int trackCount;
    private String visibility;
    private Boolean collective;
    private String created;
    private String modified;
    private Boolean isBanner;
    private Boolean isPremiere;
    private int durationMs;
    private String ogImage;
    private List<String> tags;
    private List<String> prerolls;

}
