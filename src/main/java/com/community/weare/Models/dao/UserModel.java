package com.community.weare.Models.dao;

import com.community.weare.Constrains.ValidPassword;
import com.community.weare.Models.City;
import com.community.weare.Models.Sex;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private int id;
    @Size(min = 2, message = "The username must have at least 2 symbols!")
    private String username;
    private List<String> authorities;
    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$",message = "this doesn't look like valid email")
    private String email;
    @Size(min = 2, message = "The username must have at least 2 symbols!")
    private String firstName;
    @Size(min = 2, message = "The username must have at least 2 symbols!")
    private String lastNAme;
    private Sex gender;
    @Size(min = 2, message = "The username must have at least 2 symbols!")
    private City city;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthYear;

    private String personalReview;
//    private String picture;
    private String expertise;
    private List<String>skills = new ArrayList<>();
    @DateTimeFormat(iso = DateTimeFormat.ISO.NONE,pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.NONE,pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    public UserModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastNAme() {
        return lastNAme;
    }

    public void setLastNAme(String lastNAme) {
        this.lastNAme = lastNAme;
    }

    public Sex getGender() {
        return gender;
    }

    public void setGender(Sex gender) {
        this.gender = gender;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public LocalDate getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(LocalDate birthYear) {
        this.birthYear = birthYear;
    }

    public String getPersonalReview() {
        return personalReview;
    }

    public void setPersonalReview(String personalReview) {
        this.personalReview = personalReview;
    }

//    public String getPicture() {
//        return picture;
//    }
//
//    public void setPicture(String picture) {
//        this.picture = picture;
//    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
    public void setSkill(String skill) {

        this.skills.add(skill);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
