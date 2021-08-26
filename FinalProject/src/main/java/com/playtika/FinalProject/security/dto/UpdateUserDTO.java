package com.playtika.FinalProject.security.dto;

import com.playtika.FinalProject.security.models.Role;

import java.util.ArrayList;
import java.util.List;

public class UpdateUserDTO {
    private String firstName;
    private String username;
    private String lastName;
    private String password;
    private List<Role> roles=new ArrayList<>();

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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public UpdateUserDTO(String firstName, String lastName, String password, List<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.roles = roles;
    }

    public UpdateUserDTO() {
    }
}
