package com.community.weare.Controllers;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.*;
import com.community.weare.Models.dao.UserModel;
import com.community.weare.Models.dto.ExpertiseProfileDTO;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Models.dto.UserDtoRequest;
import com.community.weare.Models.factories.ExpertiseProfileFactory;
import com.community.weare.Models.factories.PersonalProfileFactory;
import com.community.weare.Models.factories.UserFactory;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.contents.PostService;
import com.community.weare.Services.models.SkillService;
import com.community.weare.Services.users.PersonalInfoService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

import static com.community.weare.utils.HttpMessages.*;

@Controller
@RequestMapping("/auth/users")
public class ProfileController {
    private static final String TYPE = "USER";

    private final UserService userService;
    private final ExpertiseProfileFactory expertiseProfileFactory;
    private final SkillCategoryService skillCategoryService;
    private final SkillService skillService;
    private final PersonalInfoService personalInfoService;
    private final PersonalProfileFactory personalProfileFactory;
    private final UserFactory userFactory;
    private final PostService postService;

    @Autowired
    public ProfileController(UserService userService, ExpertiseProfileFactory expertiseProfileFactory, SkillCategoryService skillCategoryService, SkillService skillService, PersonalInfoService personalInfoService,
                             PersonalProfileFactory personalProfileFactory, UserFactory userFactory, PostService postService) {
        this.userService = userService;
        this.expertiseProfileFactory = expertiseProfileFactory;
        this.skillCategoryService = skillCategoryService;
        this.skillService = skillService;
        this.personalInfoService = personalInfoService;
        this.personalProfileFactory = personalProfileFactory;
        this.userFactory = userFactory;
        this.postService = postService;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView showProfilePage(@PathVariable(name = "id") int id,
                                        Principal principal) {
        ModelAndView modelAndView = new ModelAndView("profile_single");

        try {
            User user = userService.getUserById(id);
            int index = 0;
            int size = 4;
            modelAndView.addObject("index", index);
            modelAndView.addObject("size", size);

            Slice<Post> postSlice = postService.findSliceWithPosts
                    (index, size, "date", user, principal.getName());

            List<Post> postsOfUser = new ArrayList<>();

            if (postSlice.hasContent()) {
                postsOfUser = postSlice.getContent();
                index += 1;
                modelAndView.addObject("posts", postsOfUser);
                modelAndView.addObject("index", index);
            }


            modelAndView.addObject("hasNext", postSlice.hasNext());
            modelAndView.addObject("userDisable", new User());
            modelAndView.addObject("user", user);
            modelAndView.addObject("userRequest", new UserDtoRequest());
            modelAndView.addObject("friends", user.isFriend(principal.getName()));
            modelAndView.addObject("isOwner", userService.isOwner(principal.getName(), user));
            modelAndView.addObject("isAdmin", userService.isAdmin(principal));

            modelAndView.addObject("pageRequest", new Page());

        } catch (EntityNotFoundException e) {
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView.addObject("error", String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, TYPE));
        }
        return modelAndView;
    }

    @GetMapping("/{id}/profile/posts")
    public ModelAndView showProfilePosts(@PathVariable(name = "id") int id, Principal principal,
                                         @RequestParam(name = "size") int size,
                                         @RequestParam(name = "index") int index) {
        ModelAndView modelAndView = new ModelAndView("post-fragment :: postFragment");
        try {
            User user = userService.getUserById(id);
            List<Post> postsOfUser = new ArrayList<>();
            Slice<Post> postSlice = postService.findSliceWithPosts
                    (index, size, "date", user, principal.getName());

            if (postSlice.hasContent()) {
                postsOfUser = postSlice.getContent();
                index += 1;
                modelAndView.addObject("posts", postsOfUser);
                modelAndView.addObject("hasNext", postSlice.hasNext());
                modelAndView.addObject("hasPrev", postSlice.hasPrevious());
            }
            modelAndView.addObject("user", user);
            modelAndView.addObject("index", index);
            modelAndView.addObject("size", size);

        } catch (EntityNotFoundException e) {
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView.addObject("error", String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, TYPE));
        }

        return modelAndView;
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
        return "user-profile-edit";
    }


    @PostMapping("/{id}/profile/personal")
    public String editUserProfile(@PathVariable(name = "id") int id,
                                  @ModelAttribute @Valid UserModel userModel, BindingResult bindingResult, Principal principal, Model model) {
        User userToCheck = userService.getUserById(userModel.getId());
        model.addAttribute("userToEdit", userModel);

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getFieldError().getDefaultMessage());
            return "user-profile-edit";
        }
        try {

            User userToSave = userFactory.mergeUserAndModel(userToCheck, userModel);
            personalInfoService.upgradeProfile(principal.getName(), userToCheck, userToSave.getPersonalProfile());
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, TYPE));
        } catch (InvalidOperationException e) {
            model.addAttribute("error", NOT_AUTHORISED);
        }
        return "redirect:/auth/users/" + id + "/profile/editor#profile-personal";
    }

    @PostMapping("/{id}/profile/settings")
    public String editUserPicture(@PathVariable(name = "id") int id, @RequestParam("imagefile") MultipartFile file,
                                  @ModelAttribute User user, Principal principal, Model model) throws IOException {
        try {
            User userToCheck = userService.getUserById(id);
            if (file != null) {
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
    @PostMapping("/{id}/profile/expertise")
    public String editUserExpertiseProfile(@PathVariable(name = "id") int id,
                                           @ModelAttribute(name = "profileDTO") ExpertiseProfileDTO expertiseProfileDTO,
                                           BindingResult bindingResult,
                                           @ModelAttribute ExpertiseProfile expertiseProfile, Model model,
                                           Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("categories", skillCategoryService.getAll());
            return "user-profile-edit";
        }

        try {
            User userToCheck = userService.getUserById(id);
            userService.ifNotProfileOrAdminOwnerThrow(principal.getName(), userToCheck);

            if (userToCheck.getExpertiseProfile().getCategory().getName().
                    equals(expertiseProfileDTO.getCategory().getName())) {
                ExpertiseProfile expertiseProfileNew =
                        expertiseProfileFactory.convertDTOtoExpertiseProfile(expertiseProfileDTO);

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

    @ModelAttribute(name = "cities")
    public void addCitiesList(Model model) {
        List<City> cities = personalInfoService.getAllCities();
        model.addAttribute("cities", cities);
    }
}
