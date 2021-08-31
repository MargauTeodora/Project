package com.playtika.FinalProject.models.dto;

import com.playtika.FinalProject.models.User;
public class GameSessionAddDTO {
    private String gameName;
    private User user;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
