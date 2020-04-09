package com.community.weare.Controllers;


import com.community.weare.Models.dto.UserDTO;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegistrationController {
    private UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser( @ModelAttribute @Valid UserDTO user, BindingResult bindingResult, Model model) {

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("error", "Your password is not confirmed");
            return "register";
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.registerUser(user);
        return "register-confirmation";
    }
}
