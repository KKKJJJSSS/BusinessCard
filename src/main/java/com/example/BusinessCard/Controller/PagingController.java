package com.example.BusinessCard.Controller;

import com.example.BusinessCard.Dto.CardDto;
import com.example.BusinessCard.Dto.PageDto;
import com.example.BusinessCard.Mapper.PagingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PagingController {
    private final PagingMapper pagingMapper;

    private final HttpSession session;

    @GetMapping("/community")
    public String page(Model model) {
        List<PageDto> boardList = pagingMapper.getBoardList();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "login";
        }

        model.addAttribute("boardList", boardList);
        model.addAttribute("username", username);

        return "community";
    }
}