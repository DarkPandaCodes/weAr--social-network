package com.community.weare.Services.users;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.City;
import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.User;
import com.community.weare.Repositories.CityRepository;
import com.community.weare.Repositories.PersonalInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonalInfoServiceImpl implements PersonalInfoService {
    private final PersonalInfoRepository personalInfoRepository;
    private final CityRepository cityRepository;
    private final UserService userService;

    public PersonalInfoServiceImpl(PersonalInfoRepository personalInfoRepository,
                                   CityRepository cityRepository, UserService userService) {
        this.personalInfoRepository = personalInfoRepository;
        this.cityRepository = cityRepository;
        this.userService = userService;
    }
    @Transactional
    @Override
    public PersonalProfile upgradeProfile(String principal, User user, PersonalProfile personalProfile) {
        userService.ifNotProfileOrAdminOwnerThrow(principal,user);

//        PersonalProfile profileDB = personalInfoRepository.
//                findById(user.getPersonalProfile().getId()).orElseThrow(new EntityNotFoundException());
//        personalProfile.setId(profileDB.getId());

        return personalInfoRepository.saveAndFlush(personalProfile);
    }

    @Override
    public PersonalProfile createProfile(PersonalProfile personalProfile) {
        return personalInfoRepository.saveAndFlush(personalProfile);
    }

    @Override
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }
}
