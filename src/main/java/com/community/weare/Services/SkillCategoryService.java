package com.community.weare.Services;

import com.community.weare.Models.Skill;
import com.community.weare.Models.SkillCategory;

import java.util.List;
import java.util.Optional;

public interface SkillCategoryService {
    SkillCategory create(SkillCategory category);
    SkillCategory update(SkillCategory category);
    void delete(SkillCategory category);
    List<SkillCategory> getAll();
    Optional <SkillCategory> getByById(int id);
}
