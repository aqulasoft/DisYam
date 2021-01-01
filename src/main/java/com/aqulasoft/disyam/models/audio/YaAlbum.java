package com.aqulasoft.disyam.models.audio;

import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
public class YaAlbum {
    long id;
    int year;
    String genre;
    String title;
    int trackCount;
    Date releaseDate;
    List<YaArtist> artists;
}
