package com.community.weare.Models.dto;

import com.community.weare.Constrains.ValidPassword;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

public class UserDTO {

    @Size(min = 2, message = "The username must have at least 2 symbols!")
    private String username;


    @ValidPassword
    private String password;

    private String confirmPassword;

    private String email;

    private List<String> authorities;

    public UserDTO(String username, String password, List<String> authorities) {
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
