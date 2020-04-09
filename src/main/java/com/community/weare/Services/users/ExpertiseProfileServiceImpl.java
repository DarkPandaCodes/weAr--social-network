package com.community.weare.Services.users;

import com.community.weare.Models.ExpertiseProfile;
import com.community.weare.Models.User;
import com.community.weare.Repositories.ExpertiseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpertiseProfileServiceImpl implements ExpertiseProfileService {
    ExpertiseRepository expertiseRepository;

    @Autowired
    public ExpertiseProfileServiceImpl(ExpertiseRepository expertiseRepository) {
        this.expertiseRepository = expertiseRepository;
    }

    @Override
    public ExpertiseProfile upgradeProfile(User user, ExpertiseProfile expertiseProfile) {
        return null;
    }

    @Override
    public ExpertiseProfile createProfile(ExpertiseProfile expertiseProfile) {
        return expertiseRepository.saveAndFlush(expertiseProfile);
    }
}
