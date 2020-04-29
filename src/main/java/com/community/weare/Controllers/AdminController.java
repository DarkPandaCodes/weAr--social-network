package com.community.weare.Controllers;

import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.Page;
import com.community.weare.Models.Request;
import com.community.weare.Models.User;
import com.community.weare.Models.factories.ExpertiseProfileFactory;
import com.community.weare.Models.factories.UserFactory;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public String showAdminHomePage(Principal principal, Model model) {

        if (principal != null) {
            User user = userService.getUserByUserName(principal.getName());
            model.addAttribute("user", user);
            model.addAttribute("page",new Page());
        }
        return "admin";
    }

    @GetMapping("/users")
    public String showAllUsersPage(@ModelAttribute (name = "page")Page page,
                                   Model model, Principal principal) {
        try {
            User user = userService.getUserByUserName(principal.getName());
            model.addAttribute("user", user);
            List<User> userList = new ArrayList<>();
            Slice<User> userSlice = userService.findSliceWithUsers
                    (page.getIndex(), page.getSize(), "username",principal.getName(), user);

            if (userSlice.hasContent()) {
                userList = userSlice.getContent();
                page.setSize(userSlice.nextOrLastPageable().getPageSize());
                page.setIndex(userSlice.nextOrLastPageable().getPageNumber());
                model.addAttribute("users", userList);
            }
            if (userList.isEmpty()) {
                model.addAttribute("error", "There are no users");
            }
            model.addAttribute("hasNext", userSlice.hasNext());

        } catch (EntityNotFoundException e) {
          throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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
