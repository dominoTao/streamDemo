package com.chapter3;

import java.util.List;
import java.util.stream.Stream;
//专辑
public class Album {
    public String name;
    public Stream<Track> tracks;//专辑中的曲目列表
    public Stream<Artist> musicians;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Stream<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Stream<Track> tracks) {
        this.tracks = tracks;
    }

    public Stream<Artist> getMusicians() {
        return musicians;
    }

    public void setMusicians(Stream<Artist> musicians) {
        this.musicians = musicians;
    }
}
