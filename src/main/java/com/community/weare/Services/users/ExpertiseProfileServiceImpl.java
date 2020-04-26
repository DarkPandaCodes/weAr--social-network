package com.community.weare.Services.users;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.ExpertiseProfile;
import com.community.weare.Models.User;
import com.community.weare.Repositories.ExpertiseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpertiseProfileServiceImpl implements ExpertiseProfileService {
    private final ExpertiseRepository expertiseRepository;
    private final UserService userService;

    @Autowired
    public ExpertiseProfileServiceImpl(ExpertiseRepository expertiseRepository, UserService userService) {
        this.expertiseRepository = expertiseRepository;
        this.userService = userService;
    }

    @Override
    public ExpertiseProfile upgradeProfile(User user, ExpertiseProfile expertiseProfile, String principal) {
        userService.ifNotProfileOrAdminOwnerThrow(principal,user);
        ExpertiseProfile profileDB = expertiseRepository.
                findById(user.getExpertiseProfile().getId()).orElseThrow(new EntityNotFoundException());
        expertiseProfile.setId(profileDB.getId());
        return expertiseRepository.saveAndFlush(expertiseProfile);
    }

    @Transactional
    @Override
    public ExpertiseProfile createProfile(ExpertiseProfile expertiseProfile) {
        return expertiseRepository.saveAndFlush(expertiseProfile);
    }

    @Transactional
    @Override
    public ExpertiseProfile upgradeProfile(ExpertiseProfile oldProfile) {
        expertiseRepository.saveAndFlush(oldProfile);
        return oldProfile;
    }

}
