package com.chun.springsecurityoauth2.controller;

import com.chun.springsecurityoauth2.model.User;
import com.chun.springsecurityoauth2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/index")
    public User index() {
        return userService.findById(1L);
    }
}
