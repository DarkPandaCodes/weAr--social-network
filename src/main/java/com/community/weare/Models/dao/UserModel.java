package com.community.weare.Models.dao;

import com.community.weare.Constrains.ValidPassword;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.util.List;

public class UserModel {

    private int id;
    @Size(min = 2, message = "The username must have at least 2 symbols!")
    private String username;
    private List<String> authorities;

    public int getId() {
        return id;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
