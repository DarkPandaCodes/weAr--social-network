package com.community.weare.Controllers;

import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.*;
import com.community.weare.Models.dto.UserDtoRequest;
import com.community.weare.Services.connections.RequestService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;

@Controller
@RequestMapping("/auth/connection")
public class ConnectionController {
    private static final String TYPE = "USER";
    private final UserService userService;
    private RequestService requestService;

@Autowired
    public ConnectionController(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
    }

    @PostMapping("/request")
    public String sendRequest(@ModelAttribute ("userRequest") UserDtoRequest userToConnect, Principal principal) {

        try {
            User userReceiver = userService.getUserById(userToConnect.getId());
            User userSender=userService.getUserByUserName(principal.getName());
            requestService.createRequest(userSender,userReceiver);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/auth/users/" + userToConnect.getId() + "/profile";
    }
    @GetMapping("/{id}/request")
    public String getUserRequests(@PathVariable (name = "id") int id, Model model,Principal principal) {

        try {
            User user = userService.getUserById(id);
            userService.isProfileOwner(principal.getName(),user);
            model.addAttribute("requests", requestService.getAllRequestsForUserUnSeen(user));
            model.addAttribute("requestN", new Request());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        }
      return "request-list";
    }

    @PostMapping("/request/approve")
    public String getUserRequests(@ModelAttribute Request request, Principal principal) {

        try {
            if (request.isApproved()){
            Request approvedRequest= requestService.approveRequest(request.getId());
            userService.addToFriendList(approvedRequest);}
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,e.getMessage());
        }
        return "request-list";
    }


}
