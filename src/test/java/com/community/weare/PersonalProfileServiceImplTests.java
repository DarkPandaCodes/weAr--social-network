package com.community.weare;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Models.factories.UserFactory;
import com.community.weare.Repositories.CityRepository;
import com.community.weare.Repositories.ExpertiseRepository;
import com.community.weare.Repositories.PersonalInfoRepository;
import com.community.weare.Repositories.UserRepository;
import com.community.weare.Services.connections.RequestServiceImpl;
import com.community.weare.Services.users.PersonalInfoServiceImpl;
import com.community.weare.Services.users.UserService;
import com.community.weare.Services.users.UserServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.community.weare.Factory.*;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class PersonalProfileServiceImplTests {

    @InjectMocks
    PersonalInfoServiceImpl profileService;

    @Mock
    CityRepository cityRepository;

    @Mock
    PersonalInfoRepository personalInfoRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserService userService;

    @Test
    public void getAllCitiesShould_callRepository() {
        //arrange
        User user = createUser();
        user.setUserId(1);
        Principal principal = () -> "tedi";
        user = setAuthorities(user);
        PersonalProfile personalProfile = new PersonalProfile();


        //act
        profileService.getAllCities();

        //assert
        Mockito.verify(cityRepository, Mockito.times(1)).findAll();
    }


    @Test
    public void upgradeProfileShould_callRepository() {
        //arrange
        User user = createUser();
        user.setUserId(1);
        Principal principal = () -> "tedi";
        user = setAuthorities(user);
        PersonalProfile personalProfile = new PersonalProfile();

        //act
        profileService.upgradeProfile(principal.getName(), user, personalProfile);
        userService.ifNotProfileOrAdminOwnerThrow(principal.getName(),user);

        //assert
        Mockito.verify(personalInfoRepository, Mockito.times(1)).saveAndFlush(personalProfile);
    }

}
