package com.playtika.FinalProject.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CustomTime implements Comparable{

    @Column(name = "duration_hour")
    private int hour;
    @Column(name = "duration_minutes")
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


    @Override
    public int compareTo(Object o) {
        if(this.hour<((CustomTime)o).getHour()){
            return -1;
        }
        if(this.hour>((CustomTime)o).getHour()){
            return 1;
        }
        return Integer.compare(this.minutes, ((CustomTime) o).getMinutes());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CustomTime{");
        sb.append("hour=").append(hour);
        sb.append(", minutes=").append(minutes);
        sb.append('}');
        return sb.toString();
    }
}
