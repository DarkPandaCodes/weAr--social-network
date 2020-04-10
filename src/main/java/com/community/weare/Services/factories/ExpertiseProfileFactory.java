package com.community.weare.Services.factories;

import com.community.weare.Models.*;
import com.community.weare.Models.dto.ExpertiseProfileDTO;
import com.community.weare.Repositories.ResourceRepository;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class ExpertiseProfileFactory {
    private final SkillService skillService;
    private final SkillCategoryService skillCategoryService;
    private final ModelMapper modelMapper;
    private final ResourceRepository resourceRepository;

    @Autowired
    public ExpertiseProfileFactory(SkillService skillService, SkillCategoryService skillCategoryService,
                                   ModelMapper modelMapper,ResourceRepository resourceRepository) {
        this.skillService = skillService;
        this.skillCategoryService = skillCategoryService;
        this.modelMapper = modelMapper;
        this.resourceRepository=resourceRepository;
    }
@Transactional(propagation = Propagation.NESTED)
   public ExpertiseProfile convertDTOtoExpertiseProfile(ExpertiseProfileDTO profileDTO){

        ExpertiseProfile expertiseProfile=new ExpertiseProfile();

        List<Skill> skillList =new ArrayList<>();

        Category category= new Category();
        category.setCategory(profileDTO.getExpertise());

        Category skillCategory= skillCategoryService.createIfNotExist(category);
        expertiseProfile.setCategory(skillCategory);

        for (String skillValue:profileDTO.getSkills()
             ) {
            Skill skill =new Skill();
            skill.setSkill(skillValue);
            skillList.add(skill);
        }
        for (Skill skill : skillList
             ) {
            if (skillService.getByByName(skill.getSkill()).isPresent()){
             expertiseProfile.setSkill(skillService.getByByName(skill.getSkill()).get());
            }else {
                expertiseProfile.setSkill(skillService.save(skill));
            }
        }
        return expertiseProfile;
    }




}
