package com.community.weare.Models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "personal_profile")
public class PersonalProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private Sex sex;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthYear;

    private String personalReview;

    @Lob
    private String picture;

    public PersonalProfile(Integer id, String firstName, String lastName, Location location, LocalDate birthYear, String personalReview) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.birthYear = birthYear;
        this.personalReview = personalReview;
    }

    public PersonalProfile() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }
}
