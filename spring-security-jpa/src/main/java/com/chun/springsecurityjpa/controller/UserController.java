package com.chun.springsecurityjpa.controller;

import com.chun.springsecurityjpa.model.User;
import com.chun.springsecurityjpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/admin/user")
    public boolean addUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveOrupdate(user);
        return true;
    }

    @PutMapping("/admin/user")
    public boolean updateUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveOrupdate(user);
        return true;
    }

    @DeleteMapping("/admin/user")
    public void deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
    }

    @GetMapping("/admin/user")
    public User getUser(@RequestParam("id") Long id) {
        return userService.findById(id);
    }
}
