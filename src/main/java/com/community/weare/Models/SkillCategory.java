package com.community.weare.Models;



import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category_skills")
public class SkillCategory {

    @Id
    @Column(name = "skill_category_id")
    private int id;

    @Column
    private String category;


    public SkillCategory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
