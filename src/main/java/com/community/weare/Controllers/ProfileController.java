package com.community.weare.Controllers;

import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Controller
@RequestMapping("/auth/users")
public class ProfileController {
    private static final String TYPE = "USER";
    private final UserService userService;

    private final SkillCategoryService skillCategoryService;
    private SkillService skillService;

    @Autowired
    public ProfileController(UserService userService,
                             SkillCategoryService skillCategoryService,SkillService skillService) {
        this.userService = userService;
        this.skillCategoryService = skillCategoryService;
        this.skillService=skillService;
    }

    @GetMapping("/{id}/profile")
    public String showProfilePage(@PathVariable(name = "id") int id, Model model) {
        try {
            Optional<User> user = userService.getUserById(id);
            user.orElseThrow(EntityNotFoundException::new);
            model.addAttribute("user", user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return "user-profile";
    }

    @GetMapping("/{id}/profile/editor")
    public String editFormUserProfile(@PathVariable(name = "id") int id, Model model) {

//        if (bindingResult.hasErrors()) {
//            return "user-profile-edit";
//        }
        try {
            Optional<User> user = userService.getUserById(id);
            user.orElseThrow(EntityNotFoundException::new);
            model.addAttribute("userToEdit",userService.getUserModelById(id) );
            model.addAttribute("expertise",user.get().getExpertiseProfile());
            model.addAttribute("expertise",user.get().getExpertiseProfile());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        //TODO validation fields in template

        return "user-profile-edit";
    }

    @RequestMapping("/{id}/profile/change")
    public String editUserProfile(@PathVariable(name = "id") int id,
                                  @ModelAttribute UserModel userModel) {

        try {
            Optional<User> userToCheck = userService.getUserById(id);
            userToCheck.orElseThrow(EntityNotFoundException::new);
            userService.updateUserModel(userToCheck.get(),userModel);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return "redirect:/auth/users/" + id + "/profile";
    }


    @RequestMapping("/{id}/profile/expertise")
    public String editUserExpertiseProfile(@PathVariable(name = "id") int id,
                                  @ModelAttribute ExpertiseProfile expertiseProfile) {

        try {
            Optional<User> userToCheck = userService.getUserById(id);
            userToCheck.orElseThrow(EntityNotFoundException::new);
            userService.updateExpertise
                    (userToCheck.get(),expertiseProfile,userToCheck.get().getExpertiseProfile());
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
    @ModelAttribute(name = "expertise")
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
