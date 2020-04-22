package com.community.weare.Controllers;

import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.*;
import com.community.weare.Models.dto.UserDtoRequest;
import com.community.weare.Services.connections.RequestService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    @PostMapping("/request")
    public String sendRequest(@ModelAttribute("userRequest") UserDtoRequest userToConnect, Principal principal) {

        try {
            User userReceiver = userService.getUserById(userToConnect.getId());
            User userSender = userService.getUserByUserName(principal.getName());

            if (!userReceiver.isFriend(userSender.getUsername()) &&
                    requestService.getByUsers(userReceiver, userSender) == null) {
                requestService.createRequest(userSender, userReceiver);

            } else if (userReceiver.isFriend(userSender.getUsername())) {
                //TODO add confirmation
                Request request = requestService.getByUsersApproved(userReceiver, userSender);
                userService.removeFromFriendsList(request);
                requestService.deleteRequest(request);
            }
            } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/auth/users/" + userToConnect.getId() + "/profile";
    }

    @GetMapping("/{id}/request")
    public String getUserRequests(@PathVariable(name = "id") int id, Model model, Principal principal) {

        try {
            User user = userService.getUserById(id);
            userService.ifNotProfileOwnerThrow(principal.getName(), user);
            model.addAttribute("requests", requestService.getAllRequestsForUser(user));
            model.addAttribute("requestN", new Request());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        return "request-list";
    }

    @Transactional
    @PostMapping("/request/approve")
    public String getUserRequests(@ModelAttribute Request request, Principal principal,Model model) {
        Request approvedRequest=new Request();
        try {
            if (request.isApproved()) {
               approvedRequest=requestService.approveRequest(request.getId());
                userService.addToFriendList(approvedRequest);
                model.addAttribute("requests", requestService.getAllRequestsForUser(approvedRequest.getReceiver()));
            }
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
        return "redirect:/auth/connection/ " + approvedRequest.getReceiver().getUserId()  + "/request";
    }

}
