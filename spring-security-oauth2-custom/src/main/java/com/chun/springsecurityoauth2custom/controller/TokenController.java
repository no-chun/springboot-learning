package com.chun.springsecurityoauth2custom.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TokenController {

    @RequestMapping("/token")
    public Map<String, String> get(@RequestParam("code") String code, @RequestParam("state") String state) {
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("state", state);
        return map;
    }
}
