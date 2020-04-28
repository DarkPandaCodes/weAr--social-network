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

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String showAdminHomePage() {
        return "admin_users";
    }

    @GetMapping("/users")
    public String showAllUsersPage(Model model, Principal principal) {
        try {
            User user = userService.getUserByUserName(principal.getName());
            model.addAttribute("user", user);
            model.addAttribute("users", userService.getAllUsers());
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "admin_users";
    }

    @PostMapping("/status")
    public ModelAndView disableEnableUser(@ModelAttribute User user, Principal principal) {
        ModelAndView modelAndView = new ModelAndView("admin_users");
        try {
            userService.disableEnableUser(principal.getName(), user.getUserId());
        } catch (EntityNotFoundException e) {
            modelAndView.addObject("error", "User not found");
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        } catch (InvalidOperationException e) {
            modelAndView.addObject("error", "User not authorised");
            modelAndView.setStatus(HttpStatus.UNAUTHORIZED);
            return modelAndView;
        }
        return new ModelAndView("redirect:/auth/users/" + user.getUserId() + "/profile");
    }

}
