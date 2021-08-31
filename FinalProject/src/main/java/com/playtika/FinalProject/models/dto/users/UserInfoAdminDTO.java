package com.playtika.FinalProject.models.dto.users;

import com.playtika.FinalProject.models.Role;
import com.playtika.FinalProject.utils.CustomTime;

import java.util.List;

public class UserInfoAdminDTO {

    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private CustomTime maximumDailyPlayTime;
    private List<Role> roles;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CustomTime getMaximumDailyPlayTime() {
        return maximumDailyPlayTime;
    }

    public void setMaximumDailyPlayTime(CustomTime maximumDailyPlayTime) {
        this.maximumDailyPlayTime = maximumDailyPlayTime;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
