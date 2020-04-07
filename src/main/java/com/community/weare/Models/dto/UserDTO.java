package com.community.weare.Models.dto;

import java.util.Set;

public class UserDTO {

    private String username;
    private String password;
    private String email;
    private Set<RolesDTO>authorities;

    public UserDTO(String username, String password, Set<RolesDTO> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public UserDTO() {
    }

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

    public Set<RolesDTO> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<RolesDTO> authorities) {
        this.authorities = authorities;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
