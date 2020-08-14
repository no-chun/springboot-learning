package com.chun.springsecurityrememberme.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/")
    public String rout() {
        return "root";
    }

    @RequestMapping("/admin/info")
    public String index() {
        return "admin's info";
    }

    @RequestMapping("/user/info")
    public String info() {
        return "user's info";
    }

}
