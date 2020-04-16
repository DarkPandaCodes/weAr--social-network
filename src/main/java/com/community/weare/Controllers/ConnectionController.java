package com.community.weare.Controllers;

import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.*;
import com.community.weare.Models.factories.ExpertiseProfileFactory;
import com.community.weare.Services.SkillCategoryService;
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
import java.util.Collection;

@Controller
@RequestMapping("/auth")
public class ConnectionController {
    private static final String TYPE = "USER";
    private final UserService userService;
    private RequestService requestService;

@Autowired
    public ConnectionController(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
    }

    @PostMapping("/users/request")
    public String sendRequest(@ModelAttribute ("user") User userToConnect, Principal principal) {

        try {
            User userSender = userService.getUserById(userToConnect.getUserId());
            User userReceiver=userService.getUserByUserName(principal.getName());
            requestService.createRequest(userSender,userReceiver);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/auth/users/" + userToConnect.getUserId() + "/profile";
    }
    @GetMapping("/users/{id}/request")
    public String getUserRequests(@PathVariable (name = "id") int id, Model model,Principal principal) {

        try {
            User user = userService.getUserById(id);
            if (!user.getUsername().equals(principal.getName())){
                //TODO method isAuthoriesed
                throw new EntityNotFoundException();
            }
           model.addAttribute("requests", requestService.getAllRequestsForUserUnSeen(user));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());
        }
      return "";
    }

    @PostMapping("/users/request/approve")
    public String getUserRequests(@ModelAttribute Request request, Principal principal) {

        try {

            Request approvedRequest= requestService.approveRequest(request.getId());
            userService.addToFriendList(approvedRequest);
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,e.getMessage());
        }
        return "";
    }


}
