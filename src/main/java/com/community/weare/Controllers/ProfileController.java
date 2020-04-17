package com.community.weare.Controllers;

import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.ExpertiseProfileDTO;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Models.dto.UserDtoRequest;
import com.community.weare.Models.factories.ExpertiseProfileFactory;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
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

    @Autowired
    public ProfileController(UserService userService, ExpertiseProfileFactory expertiseProfileFactory,
                             SkillCategoryService skillCategoryService, SkillService skillService) {
        this.userService = userService;
        this.expertiseProfileFactory = expertiseProfileFactory;
        this.skillCategoryService = skillCategoryService;
        this.skillService = skillService;
    }

    @GetMapping("/{id}/profile")
    public String showProfilePage(@PathVariable(name = "id") int id, Model model, Principal principal) {

        try {
            User user = userService.getUserById(id);
            model.addAttribute("userRequest",new UserDtoRequest());
            model.addAttribute("user", user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "user-profile";
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
            model.addAttribute("userToEdit", userService.getUserModelById(id));
            model.addAttribute("profile", user.getExpertiseProfile());
            model.addAttribute("profileDTO", expertiseProfileDTO);
//            Category category=user.getExpertiseProfile().getCategory();
//            model.addAttribute("services",skillService.getAllByCategory(category));
            userService.isProfileOwner(principal.getName(), user);
            model.addAttribute("user", user);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }catch (InvalidOperationException e){
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,e.getMessage());
        }
        //TODO validation fields in template

        return "user-profile-edit";
    }


    @PostMapping("/{id}/profile/personal")
    public String editUserProfile(@PathVariable(name = "id") int id,
                                  @ModelAttribute UserModel userModel, Model model) {

        try {
            User userToCheck = userService.getUserById(id);

            model.addAttribute("userToEdit", userModel);
            userService.updateUserModel(userToCheck, userModel);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }catch (InvalidOperationException e){
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,e.getMessage());
        }

        return "redirect:/auth/users/" + id + "/profile/editor";
    }


    @RequestMapping("/{id}/profile/expertise")
    public String editUserExpertiseProfile(@PathVariable(name = "id") int id,
                                           @ModelAttribute ExpertiseProfileDTO expertiseProfileDTO, @ModelAttribute ExpertiseProfile expertiseProfile) {

        try {
            User userToCheck = userService.getUserById(id);


            if (expertiseProfileDTO.getSkill1() != null) {
                expertiseProfileDTO.setCategory(userToCheck.getExpertiseProfile().getCategory());
                ExpertiseProfile expertiseProfileNew =
                        expertiseProfileFactory.convertDTOtoExpertiseProfile(expertiseProfileDTO);
                userService.updateExpertise
                        (userToCheck, expertiseProfileNew, userToCheck.getExpertiseProfile());
            } else {
                userService.updateExpertise
                        (userToCheck, expertiseProfile, userToCheck.getExpertiseProfile());
            }

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/auth/users/" + id + "/profile";
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
//    @ModelAttribute(name = "skills")
//    public void addSkillsList(String category,Model model) {
//        List<Skill> skills = skillService.getAllByCategory(category);
//        model.addAttribute("skills", skills);
//    }

}
