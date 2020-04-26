package com.community.weare.Controllers;

import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Models.User;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class SearchUserController {
    private static final String TYPE = "USER";
    private final UserService userService;

    @Autowired
    public SearchUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public String getUsers(@RequestParam(name = "name", defaultValue = "") String name,
                           @RequestParam(name = "expertise", defaultValue = "") String expertise,
                           Model model) {
        //TODO add check if user is logged
        try {
//            List<User> users = userService.getPublicUsersByCriteria(name, expertise);
            List<User> users = new ArrayList<>();
            if (name != null && expertise.isEmpty()) {
                users = userService.getUserByFirstNameLastName(name);
            }
            if (expertise != null && name.isEmpty()) {
                users = userService.getUsersByExpertise(expertise);
            }
            if (name.length() >= 2 && expertise.length() >= 2) {
                users = userService.getUserByFirstNameLastName(name).stream().
                        filter(u -> u.getExpertiseProfile()
                                .getCategory().getName().equals(expertise)).
                        collect(Collectors.toList());
           }
            if (users.isEmpty()) {
                throw new EntityNotFoundException("There are no public users existing in this search criteria.");
            }
            model.addAttribute("users", users);
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "index_users";
    }
}
