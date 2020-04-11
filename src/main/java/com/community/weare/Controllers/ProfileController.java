package com.community.weare.Controllers;

import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Services.factories.ExpertiseProfileFactory;
import com.community.weare.Services.factories.PersonalProfileFactory;
import com.community.weare.Services.factories.UserFactory;
import com.community.weare.Services.users.ExpertiseProfileService;
import com.community.weare.Services.users.PersonalInfoService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Controller
@RequestMapping("/auth/users")
public class ProfileController {
    private static final String TYPE = "USER";
    private final UserService userService;
    private final PersonalInfoService personalInfoService;
    private final ExpertiseProfileService expertiseProfileService;
    private final ExpertiseProfileFactory expertiseProfileFactory;
    private final PersonalProfileFactory personalProfileFactory;
    private final UserFactory mapperHelper;

    @Autowired
    public ProfileController(UserService userService, PersonalInfoService personalInfoService, ExpertiseProfileService expertiseProfileService, ExpertiseProfileFactory expertiseProfileFactory,
                             PersonalProfileFactory personalProfileFactory, UserFactory mapperHelper) {
        this.userService = userService;
        this.personalInfoService = personalInfoService;
        this.expertiseProfileService = expertiseProfileService;
        this.expertiseProfileFactory = expertiseProfileFactory;
        this.personalProfileFactory = personalProfileFactory;
        this.mapperHelper = mapperHelper;
    }

    @GetMapping("/{id}/profile")
    public String showProfilePage(@PathVariable(name = "id") int id, Model model) {
        try {
            model.addAttribute("user",
                    userService.getUserById(id).orElseThrow(EntityNotFoundException::new));
        }catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return "user-profile";
    }

    @PostMapping("/{id}/profile")
    public String editUserProfile(@ModelAttribute @Valid UserDTO user, BindingResult bindingResult, Model model) {


        if (bindingResult.hasErrors()) {
            return "register";
        }

        return "user-profile-edit";
    }

}
