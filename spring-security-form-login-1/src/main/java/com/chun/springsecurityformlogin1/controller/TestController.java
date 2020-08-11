package com.chun.springsecurityformlogin1.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/")
    public String rout() {
        return "root";
    }

    @RequestMapping("/users")
    public String index() {
        return "users";
    }

    @RequestMapping("/failure")
    public String failure() {
        return "failure";
    }

    @RequestMapping("/fail")
    public String fail() {
        return "fail";
    }

    @RequestMapping("/logout")
    public String logout() {
        return "logout";
    }

}
