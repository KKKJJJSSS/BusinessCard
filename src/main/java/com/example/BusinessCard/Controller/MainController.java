package com.example.BusinessCard.Controller;

import com.example.BusinessCard.Mapper.CardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class MainController {
    @Autowired
    private CardMapper cardMapper;

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
    @GetMapping("/uploadcard")
    public String uploadcard(HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "login";
        }

        return "uploadcard";
    }
}
