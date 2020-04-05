package com.community.weare.Controllers;


import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller

public class Controller {
    @GetMapping
    public String showHomePage() {
        return "index";
    }

}
