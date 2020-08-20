package com.chun.filterinterceptor.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @RequestMapping("/index")
    public Map<String, String> index(HttpServletRequest request) {
        String name = (String) request.getAttribute("name");
        String password = (String) request.getAttribute("pwd");
        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        map.put("username", name);
        map.put("password", password);
        return map;
    }

    @RequestMapping("/err")
    public void err() throws Exception {
        throw new Exception("Test Error");
    }
}
