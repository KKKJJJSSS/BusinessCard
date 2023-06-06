package com.example.BusinessCard.Controller;

import com.example.BusinessCard.Dto.UserDto;
import com.example.BusinessCard.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HttpSession httpSession;

    @PostMapping("/register")
    public Map<String, String> signup(@RequestBody UserDto user) {
        Map<String, String> response = new HashMap<>();

        if (userMapper.countByUsername(user.getUsername()) == 0 &&
                userMapper.countByEmail(user.getEmail()) == 0 &&
                userMapper.countByNumber(user.getNumber()) == 0) {
            userMapper.insert(user);
            response.put("result", "success");
        } else {
            response.put("result", "fail");
        }

        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> signin(@RequestBody UserDto user) {
        Map<String, Object> response = new HashMap<>();

        UserDto foundUser = userMapper.findByUsername(user.getUsername());
        if (foundUser != null && foundUser.getPassword().equals(user.getPassword())) {
            response.put("result", "success");
            response.put("username", foundUser.getUsername()); // username 값을 응답에 추가합니다.

            httpSession.setAttribute("username", foundUser.getUsername());
        } else {
            response.put("result", "fail");
        }

        return response;
    }
}