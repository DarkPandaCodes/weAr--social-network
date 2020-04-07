package com.community.weare.Models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Size(min = 2, message = "The username must have at least 2 symbols!")
    @Column(name = "username")
    private String username;

    @Size(min = 2, message = "The username must have at least 2 symbols!")
    @Column(name = "password")
    private String password;


    @Column(name = "email")
    private String email;

    @Column(columnDefinition = "boolean default true")
    private boolean isAccountNonExpired;

    @Column(columnDefinition = "boolean default true")
    private boolean isAccountNonLocked;

    @Column(columnDefinition = "boolean default true")
    private boolean isCredentialsNonExpired;

    @Column(columnDefinition = "boolean default true")
    private boolean isEnabled;

    @ManyToMany(targetEntity = Role.class , fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id",referencedColumnName = "role_id")
    )
    private Set<Role> authorities ;


    public User() {
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }


    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }


    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }


    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
