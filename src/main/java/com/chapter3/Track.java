package com.chapter3;
//曲目
public class Track {
    public String name;
    //曲目时长
    public int trackLength;


    public Track(String name, int trackLength) {
        this.name = name;
        this.trackLength = trackLength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTrackLength() {
        return trackLength;
    }

    public void setTrackLength(int trackLength) {
        this.trackLength = trackLength;
    }
}