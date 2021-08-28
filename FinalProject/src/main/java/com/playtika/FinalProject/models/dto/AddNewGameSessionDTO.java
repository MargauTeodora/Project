package com.playtika.FinalProject.models.dto;

import com.playtika.FinalProject.models.User;
import java.sql.Time;
import java.util.Date;

public class AddNewGameSessionDTO {
    private long id;
    private String gameName;
    private Date startDate;
    private Time duration;
    private User user;

    public long getId() {
        return id;
    }

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

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
