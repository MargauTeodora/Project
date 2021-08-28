package com.playtika.FinalProject.models.dto;

public class LoginResponse {
    private String userName;
    private String email;
    private String accessToken;

    public String getUserName() {
        return userName;
    }

    public LoginResponse setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public LoginResponse setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public LoginResponse setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
