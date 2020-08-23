package com.chun.springsecurityoauth2custom.controller;

import com.chun.springsecurityoauth2custom.model.User;
import com.chun.springsecurityoauth2custom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/index")
    public User index() {
        return userService.findById(1L);
    }
}
