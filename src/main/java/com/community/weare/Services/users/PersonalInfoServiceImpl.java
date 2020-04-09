package com.community.weare.Services.users;

import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.User;
import com.community.weare.Repositories.PersonalInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class PersonalInfoServiceImpl implements PersonalInfoService {
    private final PersonalInfoRepository personalInfoRepository;


    @Autowired
    public PersonalInfoServiceImpl(PersonalInfoRepository personalInfoRepository) {
        this.personalInfoRepository = personalInfoRepository;
    }

    @Override
    public PersonalProfile upgradeProfile(User user, PersonalProfile personalProfile) {

        PersonalProfile profileDB = personalInfoRepository.findById(user.getPersonalProfile().getId()).orElseThrow(EntityNotFoundException::new);
        personalInfoRepository.saveAndFlush(profileDB);
        return profileDB;
    }

    @Override
    public PersonalProfile createProfile(PersonalProfile personalProfile) {
        return personalInfoRepository.saveAndFlush(personalProfile);
    }
}
