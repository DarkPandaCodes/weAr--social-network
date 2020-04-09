package com.community.weare.Repositories;

import com.community.weare.Models.Skill;
import com.community.weare.Models.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill,Integer> {

    Optional<Skill>findSkillByCategory(SkillCategory category);
    Optional<Skill>findSkillBySkill(String name);
}
