package com.community.weare.Services.factories;

import com.community.weare.Models.*;
import com.community.weare.Models.dto.ExpertiseProfileDTO;
import com.community.weare.Repositories.ResourceRepository;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ExpertiseProfileFactory implements ExpertiseProfileService {
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
@Override
   public ExpertiseProfile convertDTOtoExpertiseProfile(ExpertiseProfileDTO profileDTO){
    ExpertiseProfile expertiseProfile=modelMapper.map(profileDTO,ExpertiseProfile.class);
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
            if (!skillService.getByByName(skill.getSkill()).isPresent()){
                skillService.save(skill);
            }
        }
        expertiseProfile.setSkills(skillList);
        return expertiseProfile;
    }

   public ExpertiseProfile mergeExpertProfile(ExpertiseProfile newProfile,ExpertiseProfile oldProfile){
    oldProfile.setSkills(getNotNullBeer(newProfile.getSkills(),oldProfile.getSkills()));
    oldProfile.setCategory(getNotNullBeer(newProfile.getCategory(),oldProfile.getCategory()));
    oldProfile.setStartTime(getNotNullBeer(newProfile.getStartTime(),oldProfile.getStartTime()));
    oldProfile.setEndTime(getNotNullBeer(newProfile.getEndTime(),oldProfile.getEndTime()));
    return oldProfile;
    }

    public <T> T getNotNullBeer(T n, T l) {

        if (n==null || n.hashCode()==0 ){
            return l;
        }
        if (n!=null || n.hashCode()!=0 ){
            return n;
        }

        return n != null && l != null && !n.equals(l) ? n : l;

    }
}




