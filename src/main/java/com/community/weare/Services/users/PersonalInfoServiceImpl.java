package com.community.weare.Services.users;

import com.community.weare.Models.City;
import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.User;
import com.community.weare.Repositories.CityRepository;
import com.community.weare.Repositories.PersonalInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class PersonalInfoServiceImpl implements PersonalInfoService {
    private final PersonalInfoRepository personalInfoRepository;
    private final CityRepository cityRepository;

    @Autowired
    public PersonalInfoServiceImpl(PersonalInfoRepository personalInfoRepository, CityRepository cityRepository) {
        this.personalInfoRepository = personalInfoRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    public PersonalProfile upgradeProfile(User user, PersonalProfile personalProfile) {
        PersonalProfile profileDB = personalInfoRepository.
                findById(user.getPersonalProfile().getId()).orElseThrow(EntityNotFoundException::new);
        personalProfile.setId(profileDB.getId());
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
