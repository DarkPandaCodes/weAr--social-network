package com.community.weare;

import com.community.weare.Models.PersonalProfile;
import com.community.weare.Models.Request;
import com.community.weare.Models.User;
import com.community.weare.Repositories.CityRepository;
import com.community.weare.Repositories.PersonalInfoRepository;
import com.community.weare.Repositories.RequestRepository;
import com.community.weare.Repositories.UserRepository;
import com.community.weare.Services.connections.RequestServiceImpl;
import com.community.weare.Services.users.PersonalInfoServiceImpl;
import com.community.weare.Services.users.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.Principal;
import java.time.LocalDateTime;

import static com.community.weare.Factory.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestServiceImplTests {

    @InjectMocks
    RequestServiceImpl requestService;

    @Mock
    RequestRepository requestRepository;

    @Mock
    UserService userService;

    @Test
    public void getAllCitiesShould_callRepository() {
        User sender = createUser();
        User receiver = createUser2();
        Request request = new Request();
//        request.setSender(sender);
//        request.setReceiver(receiver);
//        request.setId(1);
//        request.setTimeStamp(LocalDateTime.now());
//        Principal principal = () -> "tedi";
//        user = setAuthorities(user);

        //act
//        Mockito.when(requestRepository.saveAndFlush(request)).thenReturn(request);
        requestService.createRequest(sender,receiver);

        Mockito.verify(requestRepository,Mockito.times(1)).saveAndFlush(request);

    }


//    @Test
//    public void upgradeProfileShould_callRepository() {
//        //arrange
//        User user = createUser();
//        user.setUserId(1);
//        Principal principal = () -> "tedi";
//        user = setAuthorities(user);
//        PersonalProfile personalProfile = new PersonalProfile();
//
//        //act
//        profileService.upgradeProfile(principal.getName(), user, personalProfile);
//        userService.ifNotProfileOrAdminOwnerThrow(principal.getName(),user);
//
//        //assert
//        Mockito.verify(personalInfoRepository, Mockito.times(1)).saveAndFlush(personalProfile);
//    }

}
