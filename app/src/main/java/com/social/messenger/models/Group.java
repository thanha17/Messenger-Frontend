package com.social.messenger.models;

import java.util.List;

public class Group {
    private String id;
    private String name;
    private List<String> memberIds;

    // Constructors, Getters, Setters...
    public Group() {
    }
    public Group(String id, String name, List<String> memberIds) {
        this.id = id;
        this.name = name;
        this.memberIds = memberIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }
}
