package com.chun.jdbcdemo.controller;

import com.chun.jdbcdemo.model.User;
import com.chun.jdbcdemo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {
    private final UserService userService;

    public TestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<Map<String, Object>> users() {
        return userService.queryUserListMap();
    }

    @GetMapping("/user/{id}")
    public User getById(@PathVariable Long id) {
        User user = userService.queryUserById(id);
        System.out.println(user.getName());
        return userService.queryUserById(id);
    }

    @PostMapping("/user")
    public int addUser(@RequestBody User user){
        return userService.add(user);
    }

    @DeleteMapping("/user")
    public int deleteUser(@RequestBody String id){
        return userService.deleteById(Long.parseLong(id));
    }

}
