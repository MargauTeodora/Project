package com.playtika.FinalProject.externalAPI;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Game {
    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Game() {
    }
}
