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
import java.util.Optional;

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
    public void getAllRequstsShould_callRepository() {
        User sender = createUser();
        User receiver = createUser2();
        Request request = new Request();
        request.setSender(sender);
        request.setReceiver(receiver);
        requestService.createRequest(sender,receiver);

        Mockito.verify(requestRepository,Mockito.times(1)).saveAndFlush(request);

    }



}
