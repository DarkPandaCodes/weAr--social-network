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

import static com.community.weare.utils.HttpMessages.ERROR_NOT_FOUND_MESSAGE_FORMAT;
import static com.community.weare.utils.HttpMessages.NOT_AUTHORISED;

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
    public String sendRequest(@ModelAttribute("userRequest") UserDtoRequest userToConnect, Principal principal, Model model) {

        try {
            User userReceiver = userService.getUserById(userToConnect.getId());
            User userSender = userService.getUserByUserName(principal.getName());

            if (!userReceiver.isFriend(userSender.getUsername()) &&
                    requestService.getByUsers(userReceiver, userSender) == null) {

                requestService.createRequest(userSender, userReceiver);

            } else if (userReceiver.isFriend(userSender.getUsername())) {
                Request request = requestService.getByUsersApproved(userReceiver, userSender);
                userService.removeFromFriendsList(request);
                requestService.deleteRequest(request);
            }
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, "Request"));
        }
        return "redirect:/auth/users/" + userToConnect.getId() + "/profile";
    }

    @GetMapping("/{id}/request")
    public String getUserRequests(@PathVariable(name = "id") int id, Model model, Principal principal) {

        try {
            User user = userService.getUserById(id);
            model.addAttribute("requests", requestService.getAllRequestsForUser(user, principal.getName()));
            model.addAttribute("requestN", new Request());
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, "Request"));
        }
        return "request-list";
    }

    @Transactional
    @PostMapping("/request/approve")
    public String approveRequests(@ModelAttribute Request request, Principal principal, Model model) {
        Request approvedRequest = new Request();
        try {
            if (request.isApproved()) {
                approvedRequest = requestService.approveRequest(request.getId(),request.getReceiver(),principal.getName());
                userService.addToFriendList(approvedRequest);
                model.addAttribute("requests", requestService.getAllRequestsForUser(approvedRequest.getReceiver(),principal.getName()));
            }
        } catch (InvalidOperationException e) {
            model.addAttribute("error", NOT_AUTHORISED);
        }
        return "redirect:/auth/connection/ " + approvedRequest.getReceiver().getUserId() + "/request";
    }

}
