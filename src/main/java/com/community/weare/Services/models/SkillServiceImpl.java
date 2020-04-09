package com.community.weare.Services.models;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.Skill;
import com.community.weare.Repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillServiceImpl implements SkillService {
    private SkillRepository skillRepository;

    @Autowired
    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public List<Skill> findAll(Sort sort) {
        return skillRepository.findAll(Sort.by(Sort.Direction.ASC, "skill"));
    }

    @Override
    public Skill getOne(int skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException("Skill doesnt exist");
        }
        return skillRepository.getOne(skillId);
    }

    @Override
    public Skill save(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public void editSkill(int skillId, String skill) {
        if (!skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException("Skill doesnt exist");
        }
        Skill skillToEdit = skillRepository.getOne(skillId);
        skillToEdit.setSkill(skill);
        skillRepository.save(skillToEdit);
    }

    @Override
    public void deleteSkill(int skillId) {
        if (!skillRepository.existsById(skillId)) {
            throw new EntityNotFoundException("Skill doesnt exist");
        }
        Skill skillToDelete = skillRepository.getOne(skillId);
        skillRepository.delete(skillToDelete);
    }

    @Override
    public Optional<Skill> getByByName(String name) {
        return skillRepository.findSkillBySkill(name);
    }
}
