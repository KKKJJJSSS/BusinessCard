package com.example.BusinessCard.Controller;

import com.example.BusinessCard.Dto.CardDto;
import com.example.BusinessCard.Mapper.CardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/card")
public class CardController {

    @Autowired
    private CardMapper cardMapper;

    @PostMapping("/upload")
    public Map<String, String> upload(HttpSession session, @RequestBody CardDto card) {
        Map<String, String> response = new HashMap<>();
        // 세션에서 사용자 이름을 가져오고 CardDto 객체에 설정
        String username = (String) session.getAttribute("username");
        card.setUsername(username);

        if (isInputValid(card)) {
            cardMapper.insertCard(card);
            response.put("result", "success");
        } else {
            response.put("result", "이름을 필수로 입력 해주세요.");
        }

        return response;
    }

    @PostMapping("/reupload")
    public Map<String, String> reupload(@RequestBody CardDto card) {
        Map<String, String> response = new HashMap<>();

        if (isInputValid(card)) {
            cardMapper.updateCard(card);
            response.put("result", "success");
        } else {
            response.put("result", "이름을 필수로 입력 해주세요.");
        }

        return response;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable int id) {
        try {
            cardMapper.deleteCard(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private boolean isInputValid(CardDto card) {
        return (card.getName() != null && !card.getName().trim().isEmpty());
    }
}



