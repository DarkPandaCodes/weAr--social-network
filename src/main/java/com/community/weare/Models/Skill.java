package com.community.weare.Models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "skills_table")
public class Skill {

    @Id
    @Column(name = "skill_id")
    private int skillId;

    @Column(name = "skill")
    private String skill;


}
