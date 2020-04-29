package com.community.weare.Controllers;


import com.community.weare.Models.Page;
import com.community.weare.Models.User;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Optional;

@org.springframework.stereotype.Controller

public class HomeController {
    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }


   @RequestMapping(value = {"/","/auth"},method = RequestMethod.GET)
    public String showAuthPage(Principal principal, Model model) {
        model.addAttribute("page",new Page());
        if (principal!=null){
        User user=userService.getUserByUserName(principal.getName());
        model.addAttribute("user",user);}
        return "index_new";
    }
}
