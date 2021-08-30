package com.playtika.FinalProject.models.dto;

import com.playtika.FinalProject.utils.CustomTime;

public class SignUpRequest {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private CustomTime maximumDailyPlayTime;

    public CustomTime getMaximumDailyPlayTime() {
        return maximumDailyPlayTime;
    }

    public void setMaximumDailyPlayTime(CustomTime maximumDailyPlayTime) {
        this.maximumDailyPlayTime = maximumDailyPlayTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
