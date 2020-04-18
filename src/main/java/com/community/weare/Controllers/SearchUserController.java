package com.community.weare.Controllers;

import com.community.weare.Models.User;
import com.community.weare.Services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.community.weare.utils.HttpMessages.ERROR_NOT_FOUND_MESSAGE_FORMAT;

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
        try {
            List<User> users = new ArrayList<>();
            if (name != null && expertise.isEmpty()) {

                users = userService.getUserByFirstNameLastName(name);
            }
            if (expertise != null && name.isEmpty()) {
                users = userService.getUsersByExpertise(expertise);
            }
            if (name.length()>=2 && name.length()>=2) {
                users=userService.getUserByFirstNameLastName(name).stream().
                        filter(u -> u.getExpertiseProfile()
                                .getCategory().getName().equals(expertise)).
                        collect(Collectors.toList());
            }
            model.addAttribute("users", users);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format(ERROR_NOT_FOUND_MESSAGE_FORMAT, TYPE));
        }
        return "index_users";
    }
}
