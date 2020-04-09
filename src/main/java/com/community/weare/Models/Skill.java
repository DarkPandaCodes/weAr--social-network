package com.community.weare.Models;

import javax.persistence.*;

@Entity
@Table(name = "skills_table")
public class Skill {

    @Id
    @Column(name = "skill_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int skillId;

    @Column(name = "skill")
    private String skill;

    public Skill() {
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }
}
