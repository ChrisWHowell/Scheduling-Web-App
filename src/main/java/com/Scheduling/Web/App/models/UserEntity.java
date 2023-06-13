package com.Scheduling.Web.App.models;

import java.util.List;

public class UserEntity extends BaseEntity{


    private String username;
    private String password;
    private List<Role> userRoles;

    public List<Role> getRoles() {
        return userRoles;
    }
    public void setRoles(List<Role> roles) {
        this.userRoles = roles;
    }

    // Constructors
    public UserEntity() {}

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + userRoles +
                '}';
    }
}
