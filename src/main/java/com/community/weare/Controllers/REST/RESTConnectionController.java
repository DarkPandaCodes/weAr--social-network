package com.community.weare.Controllers.REST;

import com.community.weare.Models.Request;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.UserDtoRequest;
import com.community.weare.Services.connections.RequestService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class RESTConnectionController {
    private static final String TYPE = "USER";
    private final UserService userService;
    private RequestService requestService;

@Autowired
    public RESTConnectionController(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
    }

    @PostMapping("/users/request")
    public Request sendRequest(@RequestBody UserDtoRequest user, Principal principal) {

        try {
            User userReceiver = userService.getUserById(user.getId());
            User userSender=userService.getUserByUserName(principal.getName());
           return requestService.createRequest(userSender,userReceiver);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        }

    }
    @GetMapping("/users/{id}/request/")
    public Collection<Request> getUserRequests(@PathVariable (name = "id") int id,Principal principal) {

        try {
            User user = userService.getUserById(id);
            if (!user.getUsername().equals(principal.getName())){
                throw new EntityNotFoundException();
            }
            return requestService.getAllRequestsForUserUnSeen(user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        }

    }




}
