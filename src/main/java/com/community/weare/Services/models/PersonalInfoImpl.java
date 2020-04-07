package com.community.weare.Services.models;

import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.User;
import com.community.weare.Repositories.PersonalInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonalInfoImpl implements PersonalInfoService {
    private final PersonalInfoRepository personalInfoRepository;

    @Autowired
    public PersonalInfoImpl(PersonalInfoRepository personalInfoRepository) {
        this.personalInfoRepository = personalInfoRepository;
    }

    @Override
    public PersonalProfile upgradeProfile(User user, PersonalProfile personalProfile) {

        return null;
    }
}
