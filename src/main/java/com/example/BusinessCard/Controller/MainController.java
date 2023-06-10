package com.example.BusinessCard.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainController {
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    @GetMapping("/loginpage")
    public String login() {
        return "login";
    }
    @GetMapping("/search")
    public String search() {
        return "search";
    }
}
