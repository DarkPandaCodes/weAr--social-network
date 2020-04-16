package com.community.weare.Controllers;

import com.community.weare.Models.Category;
import com.community.weare.Models.ExpertiseProfile;
import com.community.weare.Models.Sex;
import com.community.weare.Models.User;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.ExpertiseProfileDTO;
import com.community.weare.Models.factories.ExpertiseProfileFactory;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.connections.RequestService;
import com.community.weare.Services.models.SkillService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/auth/")
public class ConnectionController {
    private static final String TYPE = "USER";
    private final UserService userService;
    private final ExpertiseProfileFactory expertiseProfileFactory;
    private final SkillCategoryService skillCategoryService;
    private RequestService requestService;

@Autowired
    public ConnectionController(UserService userService, ExpertiseProfileFactory expertiseProfileFactory,
                                SkillCategoryService skillCategoryService, RequestService requestService) {
        this.userService = userService;
        this.expertiseProfileFactory = expertiseProfileFactory;
        this.skillCategoryService = skillCategoryService;
        this.requestService = requestService;
    }

    @PostMapping("/request")
    public String sendRequest(@ModelAttribute User userToConnect, Model model, Principal principal) {

        try {
            User userSender = userService.getUserById(userToConnect.getUserId()).orElseThrow(EntityNotFoundException::new);
            User userReceiver=userService.getUserByUserName(principal.getName());
        requestService.createRequest(userSender,userReceiver);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "user-profile";
    }




}
