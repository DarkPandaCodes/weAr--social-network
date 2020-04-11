package com.community.weare.Models;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @Column(name = "skill_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer skillId;
    @Size(min = 2)
    @Column(name = "skill")
    private String skill;


    public Skill() {
    }

    public Integer getSkillId() {
        return skillId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

}
