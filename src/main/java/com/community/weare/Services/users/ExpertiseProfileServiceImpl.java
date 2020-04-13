package com.community.weare.Services.users;

import com.community.weare.Models.ExpertiseProfile;
import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.User;
import com.community.weare.Repositories.ExpertiseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class ExpertiseProfileServiceImpl implements ExpertiseProfileService {
    ExpertiseRepository expertiseRepository;

    @Autowired
    public ExpertiseProfileServiceImpl(ExpertiseRepository expertiseRepository) {
        this.expertiseRepository = expertiseRepository;
    }

    @Override
    public ExpertiseProfile upgradeProfile(User user, ExpertiseProfile expertiseProfile) {
        ExpertiseProfile profileDB = expertiseRepository.
                findById(user.getExpertiseProfile().getId()).orElseThrow(EntityNotFoundException::new);
        expertiseProfile.setId(profileDB.getId());
        return expertiseRepository.saveAndFlush(expertiseProfile);
    }

    @Override
    public ExpertiseProfile createProfile(ExpertiseProfile expertiseProfile) {
        return expertiseRepository.saveAndFlush(expertiseProfile);
    }

    @Transactional
    @Override
    public ExpertiseProfile upgradeProfile(ExpertiseProfile newProfile, ExpertiseProfile oldProfile) {
        oldProfile.setSkills(getNotNull(newProfile.getSkills(),oldProfile.getSkills()));
        oldProfile.setCategory(getNotNull(newProfile.getCategory(),oldProfile.getCategory()));
        oldProfile.setStartTime(getNotNull(newProfile.getStartTime(),oldProfile.getStartTime()));
        oldProfile.setEndTime(getNotNull(newProfile.getEndTime(),oldProfile.getEndTime()));
        expertiseRepository.saveAndFlush(oldProfile);
        return oldProfile;
    }



    public <T> T getNotNull(T n, T l) {

        if (n==null || n.hashCode()==0 ){
            return l;
        }
        if (n!=null || n.hashCode()!=0 ){
            return n;
        }

        return n != null && l != null && !n.equals(l) ? n : l;

    }
}
