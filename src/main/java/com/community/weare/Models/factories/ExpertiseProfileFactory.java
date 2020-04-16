package com.community.weare.Models.factories;

import com.community.weare.Models.*;
import com.community.weare.Models.dto.ExpertiseProfileDTO;
import com.community.weare.Repositories.ResourceRepository;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

import static com.community.weare.Models.factories.FactoryUtils.getNotNull;

@Service
public class ExpertiseProfileFactory  {
    private final SkillService skillService;
    private final SkillCategoryService skillCategoryService;
    private final ModelMapper modelMapper;
    private final ResourceRepository resourceRepository;

    @Autowired
    public ExpertiseProfileFactory(SkillService skillService, SkillCategoryService skillCategoryService,
                                   ModelMapper modelMapper, ResourceRepository resourceRepository) {
        this.skillService = skillService;
        this.skillCategoryService = skillCategoryService;
        this.modelMapper = modelMapper;
        this.resourceRepository=resourceRepository;
    }

@Transactional
   public ExpertiseProfile convertDTOtoExpertiseProfile(ExpertiseProfileDTO profileDTO){

        ExpertiseProfile expertiseProfile=new ExpertiseProfile();

        List<Skill> skillList =new ArrayList<>();

        Category skillCategory= skillCategoryService.createIfNotExist(profileDTO.getCategory());

        profileDTO.setSkills();

    for (String skillValue:profileDTO.getSkills()) {
        if (!skillValue.isEmpty()){
        Skill skill =new Skill();
        skill.setSkill(skillValue);
        skillList.add(skill); }
    }
        for (Skill skill : skillList
             ) {
            if (!skillService.getByName(skill.getSkill().toLowerCase()).isPresent()){
                skill.setCategory(skillCategory);
                skillService.save(skill);
            } }
        expertiseProfile.setStartTime(profileDTO.getStartTime());
        expertiseProfile.setEndTime(profileDTO.getEndTime());
        expertiseProfile.setSkills(skillList);
        return expertiseProfile;
    }

   public ExpertiseProfile mergeExpertProfile(ExpertiseProfile newProfile,ExpertiseProfile oldProfile){
    oldProfile.setSkills(getNotNull(newProfile.getSkills(),oldProfile.getSkills()));
    oldProfile.setCategory(getNotNull(newProfile.getCategory(),oldProfile.getCategory()));
    oldProfile.setStartTime(getNotNull(newProfile.getStartTime(),oldProfile.getStartTime()));
    oldProfile.setEndTime(getNotNull(newProfile.getEndTime(),oldProfile.getEndTime()));
    return oldProfile;
    }


}




