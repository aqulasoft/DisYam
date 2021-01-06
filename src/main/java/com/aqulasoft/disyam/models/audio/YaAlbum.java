package com.aqulasoft.disyam.models.audio;

import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
public class YaAlbum {
    private long id;
    private int year;
    private String genre;
    private String title;
    private int trackCount;
    private Date releaseDate;
    private List<YaArtist> artists;
}
