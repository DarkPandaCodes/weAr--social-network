package com.community.weare.Services.factories;

import com.community.weare.Models.*;
import com.community.weare.Models.dto.ExpertiseProfileDTO;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ExpertiseProfileFactory {
    private final SkillService skillService;
    private final SkillCategoryService skillCategory;
    private final ModelMapper modelMapper;

    @Autowired
    public ExpertiseProfileFactory(SkillService skillService, SkillCategoryService skillCategory,
                                   ModelMapper modelMapper) {
        this.skillService = skillService;
        this.skillCategory = skillCategory;
        this.modelMapper = modelMapper;
    }
    ExpertiseProfile convertDTOtoExpertiseProfile(ExpertiseProfileDTO profileDTO){
        ExpertiseProfile expertiseProfile=new ExpertiseProfile();
        List<Skill>skillList=new ArrayList<>();

        for (String skillValue:profileDTO.getSkills()
             ) {
            Skill skill=new Skill();
            skill.setSkill(skillValue);
            skillList.add(skill);
        }
        for (Skill skill:skillList
             ) {
            if (skillService.getByByName(skill.getSkill()).isPresent()){
             expertiseProfile.setSkill(skill);}
            //TODO check if throw is needed
//            }else {
//                throw new EntityNotFoundException("Skill", "name",skill.getSkill());
//            }
        }
        Resource resource=new Resource();
        resource.setStartTime(profileDTO.getStartTime());
        resource.setEndTime(profileDTO.getEndTime());
        expertiseProfile.setResource(resource);
        return expertiseProfile;
    }




}
