package com.playtika.FinalProject.models;

import java.util.List;

public abstract class GeneralUser {
    private List<Role> roles;

    public List<Role> getRoles() {
        return roles;
    }
    public boolean isAdmin(){
        return roles.contains(new Role(RoleType.ROLE_ADMIN.name()));
    }
    public boolean isManager(){
        return roles.contains(new Role(RoleType.ROLE_MANAGER.name()));
    }
}
