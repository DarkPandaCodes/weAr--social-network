package com.community.weare.Models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Entity
@Table(name = "expertise_profile")
public class ExpertiseProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Skill> skills;


    @ManyToOne
    private Category category;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH")
    private LocalDateTime endTime;


    public ExpertiseProfile() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public void setSkill(Skill skill) {
        this.skills.add(skill);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String calculateAvailability() {
        Duration duration = Duration.between(startTime, endTime);
        long hours = Math.abs(duration.toHours());
        return String.format("%d hours", hours);
    }
}
