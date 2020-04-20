package com.community.weare.Controllers;

import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.ExpertiseProfileDTO;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Models.dto.UserDtoRequest;
import com.community.weare.Models.factories.ExpertiseProfileFactory;
import com.community.weare.Models.factories.UserFactory;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
import com.community.weare.Services.users.UserService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/auth/users")
public class ProfileController {
    private static final String TYPE = "USER";
    private final UserService userService;
    private final ExpertiseProfileFactory expertiseProfileFactory;
    private final SkillCategoryService skillCategoryService;
    private SkillService skillService;
    private UserFactory userFactory;

    @Autowired
    public ProfileController(UserService userService, ExpertiseProfileFactory expertiseProfileFactory,
                             SkillCategoryService skillCategoryService, SkillService skillService,UserFactory userFactory) {
        this.userService = userService;
        this.expertiseProfileFactory = expertiseProfileFactory;
        this.skillCategoryService = skillCategoryService;
        this.skillService = skillService;
        this.userFactory= userFactory;
    }

    @GetMapping("/{id}/profile")
    public String showProfilePage(@PathVariable(name = "id") int id, Model model, Principal principal) {
        try {
            User user = userService.getUserById(id);
            model.addAttribute("userRequest", new UserDtoRequest());
            model.addAttribute("user", user);

            boolean areFriends = false;

            if (user.isFriend(principal.getName())) {
                areFriends = true;
            }
            model.addAttribute("friends", areFriends);
            model.addAttribute("isOwner", userService.isOwner(principal.getName(), user));

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "profile_new";
    }

    @GetMapping("/{id}/userImage")
    public void renderPostImageFormDB(@PathVariable int id, HttpServletResponse response) throws IOException {
        User user = userService.getUserById(id);
        if (user.getPersonalProfile().getPicture() != null) {
            response.setContentType("image/jpeg");
            InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(user.getPersonalProfile().getPicture()));
            IOUtils.copy(is, response.getOutputStream());
        }
    }

    @GetMapping("/{id}/profile/editor")
    public String editFormUserProfile(@PathVariable(name = "id") int id, Model model, Principal principal,
                                      @ModelAttribute @Valid UserDTO user1,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user-profile-edit";
        }
        try {
            User user = userService.getUserById(id);
            ExpertiseProfileDTO expertiseProfileDTO = new ExpertiseProfileDTO();
            expertiseProfileDTO.setId(user.getExpertiseProfile().getId());
            expertiseProfileDTO.setCategory(user.getExpertiseProfile().getCategory());
            model.addAttribute("userToEdit", userService.getUserModelById(id));
            model.addAttribute("profile", user.getExpertiseProfile());
            model.addAttribute("profileDTO", expertiseProfileDTO);
            userService.isProfileOwner(principal.getName(), user);
            model.addAttribute("user", user);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage());
        }
        //TODO validation fields in template

        return "user-profile-edit";
    }


    @PostMapping("/{id}/profile/personal")
    public String editUserProfile(@PathVariable(name = "id") int id, @RequestParam("imagefile") MultipartFile file,
                                  @ModelAttribute UserModel userModel, BindingResult bindingResult, Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            return "user-profile-edit";
        }
        try {
            User userToCheck = userService.getUserById(id);
            model.addAttribute("userToEdit", userModel);
            if (file != null) {
                userToCheck.getPersonalProfile().setPicture(Base64.getEncoder().encodeToString(file.getBytes()));
            }
            User userToSave= userFactory.mergeUserAndModel(userToCheck, userModel);
            userService.updateUser(userToSave);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, e.getMessage());
        }

        return "redirect:/auth/users/" + id + "/profile/editor#profile-services";
    }

    @Transactional
    @RequestMapping("/{id}/profile/expertise")
    public String editUserExpertiseProfile(@PathVariable(name = "id") int id,
                                           @ModelAttribute(name = "profileDTO") ExpertiseProfileDTO expertiseProfileDTO, @ModelAttribute ExpertiseProfile expertiseProfile) {

        try {
            User userToCheck = userService.getUserById(id);

            if (userToCheck.getExpertiseProfile().getCategory().getName().
                    equals(expertiseProfileDTO.getCategory().getName())) {

                ExpertiseProfile expertiseProfileNew =
                        expertiseProfileFactory.convertDTOtoExpertiseProfile(expertiseProfileDTO);
                ExpertiseProfile expertiseProfileMerged =
                        expertiseProfileFactory.mergeExpertProfile(expertiseProfileNew,
                                userToCheck.getExpertiseProfile());
                userService.updateExpertise
                        (userToCheck, expertiseProfileMerged );
            } else {
                ExpertiseProfile expertiseProfileMerged =
                        expertiseProfileFactory.mergeExpertProfile(expertiseProfile,
                                userToCheck.getExpertiseProfile());
                userService.updateExpertise
                        (userToCheck, expertiseProfile);
            }

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/auth/users/" + id + "/profile#profile";
    }


    @ModelAttribute(name = "genders")
    public void addGenderList(Model model) {
        List<Sex> genders = Arrays.asList(Sex.values());
        model.addAttribute("genders", genders);
    }

    @ModelAttribute(name = "categories")
    public void addExpertiseList(Model model) {
        List<Category> expertise = skillCategoryService.getAll();
        model.addAttribute("categories", expertise);
    }

}
