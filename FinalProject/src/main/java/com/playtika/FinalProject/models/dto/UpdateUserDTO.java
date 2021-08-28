package com.playtika.FinalProject.models.dto;

import com.playtika.FinalProject.models.RoleType;

public class UpdateUserDTO {
    private String firstName;
    private String username;
    private String lastName;
    private String password;
//    private List<Role> roles;
    private RoleType role;

    public String getFirstName() {
        return firstName;
    }

    public String getUsername() {
        return username;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public UpdateUserDTO(String firstName, String lastName, String password, RoleType role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
    }

    public UpdateUserDTO() {
    }
}
