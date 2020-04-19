package com.community.weare.Controllers;


import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Models.Category;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Repositories.SkillCategoryRepository;
import com.community.weare.Services.SkillCategoryService;
import com.community.weare.Services.models.SkillService;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class RegistrationController {
    private UserService userService;
    private SkillCategoryService skillCategoryService;

    @Autowired
    public RegistrationController(UserService userService, SkillCategoryService skillCategoryService) {
        this.userService = userService;
        this.skillCategoryService = skillCategoryService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new UserDTO());

        return "register-new";
    }

    @ModelAttribute(name = "categories")
    public void addExpertiseList(Model model) {
        List<Category> expertise = skillCategoryService.getAll();
        model.addAttribute("categories", expertise);
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@ModelAttribute @Valid UserDTO user, BindingResult bindingResult) {
        ModelAndView modelAndViewError = new ModelAndView("register-new");
        modelAndViewError.addObject("user",user);
        ModelAndView modelAndView = new ModelAndView("confirmation-new");

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            modelAndViewError.addObject("error", "Your password is not confirmed");
            modelAndViewError.setStatus(HttpStatus.BAD_REQUEST);
            return modelAndViewError;
        }
        if (bindingResult.hasErrors()) {
          modelAndViewError.addObject("error",bindingResult.getFieldError().getDefaultMessage());
          modelAndViewError.setStatus(HttpStatus.BAD_GATEWAY);
          return modelAndViewError;
        }
        try {
            int id = userService.registerUser(user);
            modelAndView.addObject("id", id);
            return modelAndView;
        } catch (DuplicateEntityException e) {
            modelAndViewError.addObject("error", e.getMessage());
            modelAndViewError.setStatus(HttpStatus.CONFLICT);
            return modelAndViewError;
        }

    }
}
