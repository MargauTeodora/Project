package com.playtika.FinalProject.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;


@Entity
@Table(name = "gameSessions")
public class GameSession {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    //    @Column(name = "duration")
//    private Time duration;

    @Embedded
    @Column(name = "duration")
    private CustomTime duration=new CustomTime();


    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public CustomTime getDuration() {
        return duration;
    }

    public void setDuration(CustomTime duration) {
        this.duration = duration;
    }

    //    public Time getDuration() {
//        return duration;
//    }
//
//    public void setDuration(Time duration) {
//        this.duration = duration;
//    }

}
