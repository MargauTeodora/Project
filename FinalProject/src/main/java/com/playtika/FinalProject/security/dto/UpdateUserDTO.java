package com.playtika.FinalProject.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.playtika.FinalProject.security.models.Role;

import javax.persistence.*;
import java.util.List;

public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String password;


    @JsonIgnore
    @ManyToMany(fetch= FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;
}
