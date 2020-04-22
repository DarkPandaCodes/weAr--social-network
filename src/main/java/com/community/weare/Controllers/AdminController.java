package com.community.weare.Controllers;

import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.User;
import com.community.weare.Models.factories.ExpertiseProfileFactory;
import com.community.weare.Models.factories.UserFactory;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.Collection;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final String TYPE = "USER";
    private final UserService userService;
    private final ExpertiseProfileFactory expertiseProfileFactory;
    private final SkillCategoryService skillCategoryService;
    private SkillService skillService;
    private UserFactory userFactory;

    @Autowired
    public AdminController(UserService userService, ExpertiseProfileFactory expertiseProfileFactory,
                           SkillCategoryService skillCategoryService, SkillService skillService, UserFactory userFactory) {
        this.userService = userService;
        this.expertiseProfileFactory = expertiseProfileFactory;
        this.skillCategoryService = skillCategoryService;
        this.skillService = skillService;
        this.userFactory= userFactory;
    }

    @GetMapping("")
    public String showAdminHomePage( ) {
        return "admin_users";
    }

    @GetMapping("/users")
    public String showAllUsersPage( Model model, Principal principal) {
        try {
            User user=userService.getUserByUserName(principal.getName());
            model.addAttribute("user",user);
            Collection<User> usersAll = userService.getAllUsers();
            model.addAttribute("users",usersAll);
            model.addAttribute("isAdmin", userService.isAdmin(principal));

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "admin_users";
    }

    @PostMapping("/status")
    public ModelAndView disableEnableUser(@ModelAttribute User user,  Principal principal){
        ModelAndView modelAndView=new ModelAndView("admin_users");
        try {
            modelAndView.addObject("userDisable",userService.getUserById(user.getUserId()));
            userService.ifNotProfileOrAdminOwnerThrow(principal.getName(),user);
//            //TODO ask if is good practise
//            modelAndView.addObject("isEnabled",user.isEnabled());
            userService.disableEnableUser(user.getUserId());
            userService.updateUser(user);
        }catch (EntityNotFoundException e){
            modelAndView.addObject("error","User not found");
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }catch (InvalidOperationException e){
            modelAndView.addObject("error","User not authorised");
            modelAndView.setStatus(HttpStatus.UNAUTHORIZED);
            return modelAndView;
        }
      return new ModelAndView("redirect:/admin/status#enabled");
    }



}
