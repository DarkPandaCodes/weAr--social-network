package com.community.weare.Models.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ExpertiseProfileDTO {

    private List<String> skills;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public ExpertiseProfileDTO() {
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }


}
