package com.playtika.FinalProject.models.dto;

public class GameSessionUsersDTO extends GameSessionInfoDTO{
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
