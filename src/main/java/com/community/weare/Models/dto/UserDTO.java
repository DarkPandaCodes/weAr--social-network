package com.community.weare.Models.dto;

import com.community.weare.Constrains.ValidPassword;

import javax.validation.constraints.Size;
import java.util.Set;

public class UserDTO {

    @Size(min = 2, message = "The username must have at least 2 symbols!")
    private String username;

    @ValidPassword
    private String password;

    private String confirmPassword;

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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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
