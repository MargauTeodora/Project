package com.playtika.FinalProject.models.dto.game;

import com.playtika.FinalProject.utils.CustomTime;

public class GameSessionInfoDTO {
    private String gameName;
    private String startDate;
    private CustomTime duration;


    public GameSessionInfoDTO(String gameName, String startDate, CustomTime duration) {
        this.gameName = gameName;
        this.startDate = startDate;
        this.duration = duration;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public CustomTime getDuration() {
        return duration;
    }

    public void setDuration(CustomTime duration) {
        this.duration = duration;
    }

    public GameSessionInfoDTO() {
    }
}
