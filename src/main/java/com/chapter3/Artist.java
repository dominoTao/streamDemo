package com.chapter3;

import java.util.List;
import java.util.stream.Stream;

//表演者
public class Artist {
    public String name;
    public List<String> members;
    public Stream<Artist> membersStream;
    public String origin;//国籍

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public Stream<Artist> getMembersStream() {
        return membersStream;
    }

    public void setMembersStream(Stream<Artist> membersStream) {
        this.membersStream = membersStream;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
