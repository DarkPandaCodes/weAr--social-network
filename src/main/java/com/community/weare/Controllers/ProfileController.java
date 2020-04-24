package com.community.weare.Controllers;

import com.community.weare.Exceptions.EntityNotFoundException;
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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.*;

import static com.community.weare.utils.HttpMessages.ERROR_NOT_FOUND_MESSAGE_FORMAT;

@Controller
@RequestMapping("/auth/users")
public class ProfileController {
    private static final String TYPE = "USER";
    private static final Object NOT_AUTHORISED = "User is not authorised";
    private final UserService userService;
    private final ExpertiseProfileFactory expertiseProfileFactory;
    private final SkillCategoryService skillCategoryService;
    private SkillService skillService;
    private UserFactory userFactory;

    @Autowired
    public ProfileController(UserService userService, ExpertiseProfileFactory expertiseProfileFactory,
                             SkillCategoryService skillCategoryService, SkillService skillService, UserFactory userFactory) {
        this.userService = userService;
        this.expertiseProfileFactory = expertiseProfileFactory;
        this.skillCategoryService = skillCategoryService;
        this.skillService = skillService;
        this.userFactory = userFactory;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView showProfilePage(@PathVariable(name = "id") int id, Principal principal) {
        ModelAndView modelAndView = new ModelAndView("profile_single");
        try {
            User user = userService.getUserById(id);
            modelAndView.addObject("userRequest", new UserDtoRequest());
            modelAndView.addObject("userDisable", new User());
            modelAndView.addObject("user", user);
            modelAndView.addObject("friends", user.isFriend(principal.getName()));
            modelAndView.addObject("isOwner", userService.isOwner(principal.getName(), user));
            modelAndView.addObject("isAdmin", userService.isAdmin(principal));

        } catch (EntityNotFoundException e) {
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView.addObject("error", String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, TYPE));
        }
        return modelAndView;
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
            userService.ifNotProfileOrAdminOwnerThrow(principal.getName(), user);
            ExpertiseProfileDTO expertiseProfileDTO = new ExpertiseProfileDTO();
            expertiseProfileDTO.setId(user.getExpertiseProfile().getId());
            expertiseProfileDTO.setCategory(user.getExpertiseProfile().getCategory());
            model.addAttribute("userToEdit", userService.getUserModelById(id));
            model.addAttribute("profile", user.getExpertiseProfile());
            model.addAttribute("profileDTO", expertiseProfileDTO);
            model.addAttribute("user", user);

        } catch (EntityNotFoundException e) {
            model.addAttribute("error", String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, TYPE));
        } catch (InvalidOperationException e) {
            model.addAttribute("error", NOT_AUTHORISED);
        }
        //TODO validation fields in template

        return "user-profile-edit";
    }


    @PostMapping("/{id}/profile/personal")
    public String editUserProfile(@PathVariable(name = "id") int id,
                                  @ModelAttribute UserModel userModel, BindingResult bindingResult, Principal principal, Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            return "user-profile-edit";
        }
        try {
            User userToCheck = userService.getUserById(userModel.getId());
            model.addAttribute("userToEdit", userModel);
            User userToSave = userFactory.mergeUserAndModel(userToCheck, userModel);
            userService.updateUser(userToSave, principal.getName(), userToCheck);

        } catch (EntityNotFoundException e) {
            model.addAttribute("error", String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, TYPE));
        } catch (InvalidOperationException e) {
            model.addAttribute("error", NOT_AUTHORISED);
        }
        return "redirect:/auth/users/" + userModel.getId() + "/profile/editor#profile-personal";
    }

    @PostMapping("/{id}/profile/settings")
    public String editUserPicture(@PathVariable(name = "id") int id, @RequestParam("imagefile") MultipartFile file,
                                  @ModelAttribute User user, Principal principal, Model model) throws IOException {
        try {
            User userToCheck = userService.getUserById(id);
            if (file != null) {
                System.out.println("snimka" + file.toString());
                userToCheck.getPersonalProfile().setPicture(Base64.getEncoder().encodeToString(file.getBytes()));
                userToCheck.getPersonalProfile().setPicturePrivacy(user.getPersonalProfile().isPicturePrivacy());
            }
            userService.updateUser(userToCheck, principal.getName(), userToCheck);
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, TYPE));
        } catch (InvalidOperationException e) {
            model.addAttribute("error", NOT_AUTHORISED);
        }

        return "redirect:/auth/users/" + id + "/profile";
    }

    @Transactional
    @RequestMapping("/{id}/profile/expertise")
    public String editUserExpertiseProfile(@PathVariable(name = "id") int id,
                                           @ModelAttribute(name = "profileDTO") ExpertiseProfileDTO expertiseProfileDTO,
                                           BindingResult bindingResult,
                                           @ModelAttribute ExpertiseProfile expertiseProfile, Model model,
                                           Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getFieldError().getDefaultMessage());
            return "user-profile-edit";
        }

        try {
            User userToCheck = userService.getUserById(id);
            userService.ifNotProfileOrAdminOwnerThrow(principal.getName(), userToCheck);

            if (userToCheck.getExpertiseProfile().getCategory().getName().
                    equals(expertiseProfileDTO.getCategory().getName())) {
                ExpertiseProfile expertiseProfileNew =
                        expertiseProfileFactory.convertDTOtoExpertiseProfile(expertiseProfileDTO);

                //TODO test when skill is duplicated, throw exception
                ExpertiseProfile expertiseProfileMerged =
                        expertiseProfileFactory.mergeExpertProfile(expertiseProfileNew,
                                userToCheck.getExpertiseProfile());

                userService.updateExpertise
                        (userToCheck, expertiseProfileMerged, principal.getName(), userToCheck);
            } else {
                userService.updateExpertise
                        (userToCheck, expertiseProfile, principal.getName(), userToCheck);
            }
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, TYPE));
        } catch (InvalidOperationException e) {
            model.addAttribute("error", NOT_AUTHORISED);
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
