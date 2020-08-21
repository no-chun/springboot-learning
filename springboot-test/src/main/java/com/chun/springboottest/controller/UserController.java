package com.chun.springboottest.controller;

import com.chun.springboottest.model.User;
import com.chun.springboottest.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("user/{userName}")
    public User getUserByName(@PathVariable(value = "userName") String userName) {
        return this.userService.getByName(userName);
    }

    @PostMapping("user/save")
    public void saveUser(@RequestBody User user) {
        this.userService.add(user);
    }
}
