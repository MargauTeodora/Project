package com.playtika.FinalProject.models.dto.users;

import com.playtika.FinalProject.utils.CustomTime;

public class UserInfoDTO {
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private CustomTime maximumDailyPlayTime=new CustomTime(0,0);


    public UserInfoDTO(String userName, String email, String firstName, String lastName, CustomTime maximumDailyPlayTime) {
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.maximumDailyPlayTime = maximumDailyPlayTime;
    }

    public UserInfoDTO() {
    }

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
