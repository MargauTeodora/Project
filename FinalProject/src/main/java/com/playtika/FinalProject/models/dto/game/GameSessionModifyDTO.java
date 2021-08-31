package com.playtika.FinalProject.models.dto.game;

import com.playtika.FinalProject.models.GameSession;
import com.playtika.FinalProject.utils.CustomTime;
import java.util.Date;

public class GameSessionModifyDTO {

    private String gameName;

    private Date startDate;

    private CustomTime duration;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public CustomTime getDuration() {
        return duration;
    }

    public void setDuration(CustomTime duration) {
        this.duration = duration;
    }


    public GameSession convertToGameSession(){
        GameSession gameSession=new GameSession();
        if(this.gameName!=null){
            gameSession.setGameName(this.gameName);
        }
        if(this.startDate!=null){
            gameSession.setStartDate(this.startDate);
        } if(this.duration!=null){
            gameSession.setDuration(this.duration);
        }
        return gameSession;
    }
}
