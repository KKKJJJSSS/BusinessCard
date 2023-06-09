package com.example.BusinessCard.Controller;

import com.example.BusinessCard.Dto.CardDto;
import com.example.BusinessCard.Mapper.CardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class BoardController {
    @Autowired
    CardMapper cardMapper;

    @Autowired
    HttpSession httpSession;
    @GetMapping("/board")
    public String getBoard(HttpSession session, Model model) {
        List<CardDto> cardList = cardMapper.getCardList();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "login";
        }

        model.addAttribute("cardList", cardList);
        model.addAttribute("username", username);

        return "card";
    }
    @GetMapping("/reupload")
    public String getRetouch(HttpSession session, Model model, @RequestParam("id") int id) {
        List<CardDto> cardList = cardMapper.getCardList();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "login";
        }

        model.addAttribute("cardList", cardList);
        model.addAttribute("username", username);
        model.addAttribute("id", id);

        return "reupload";
    }
}
