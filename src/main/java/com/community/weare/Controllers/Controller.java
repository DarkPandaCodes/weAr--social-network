package com.community.weare.Controllers;


import com.community.weare.Models.User;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@org.springframework.stereotype.Controller

public class Controller {
    private final UserService userService;

    @Autowired
    public Controller(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @RequestMapping(value = {"/auth"},method = RequestMethod.GET)
    public String showAuthPage(Principal principal, Model model) {
        User user=userService.getUserByUserName(principal.getName());
        model.addAttribute("user",user);
        return "index";
    }
}
