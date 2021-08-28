package com.playtika.FinalProject.models;

import javax.persistence.Embeddable;

@Embeddable
public class CustomTime {

    private int hour;
    private int minutes;

    public CustomTime(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
    }
    public CustomTime() {
        this.hour = 0;
        this.minutes = 0;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
