package com.community.weare.Models.factories;

import com.community.weare.Models.*;
import com.community.weare.Models.dto.PersonalProfileDTO;
import com.community.weare.Repositories.CityRepository;
import com.community.weare.Repositories.LocationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PersonalProfileFactory {
    private final LocationRepository locationRepository;
    private final CityRepository cityRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PersonalProfileFactory(LocationRepository locationRepository, CityRepository cityRepository, ModelMapper modelMapper) {
        this.locationRepository = locationRepository;
        this.cityRepository = cityRepository;
        this.modelMapper = modelMapper;
    }

    public LocalDate createBirthYear(LocalDate birthYear) {
        return null;
    }


    public Location createLocation(String cityName) {
        Location location=new Location();
        City city=new City();
        city.setCity(cityName);
        //TODO check if exist if not create
        cityRepository.saveAndFlush(city);
        location.setCity(city);
        locationRepository.saveAndFlush(location);
        return location;
    }

    public PersonalProfile covertDTOtoPersonalProfile(PersonalProfileDTO personalProfileDTO){
        PersonalProfile personalProfile=modelMapper.map(personalProfileDTO,PersonalProfile.class);
        personalProfile.setBirthYear(personalProfileDTO.getBirthday());
        Location location= createLocation(personalProfileDTO.getCity());
        Sex sex=Sex.valueOf(personalProfileDTO.getSex().toUpperCase());
        personalProfile.setSex(sex);
        personalProfile.setLocation(location);
        return personalProfile;
    }
}
