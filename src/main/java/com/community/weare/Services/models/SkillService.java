package com.community.weare.Services.models;

import com.community.weare.Models.Post;
import com.community.weare.Models.Skill;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.PostDTO;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SkillService {

    List<Skill> findAll(Sort sort);

    Skill getOne(int skillId);

    Skill save(Skill skill);

    void editSkill(int skillId, String skill);

    void deleteSkill(int skillId);
}
