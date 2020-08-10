package com.chun.controlleradvice.controller;

import com.chun.controlleradvice.exception.CustomException;
import com.chun.controlleradvice.model.Result;
import com.chun.controlleradvice.model.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TestController {

    @RequestMapping("/err/1")
    public void err1() {
        throw new Error("error 1");
    }

    @RequestMapping("/err/2")
    public void err2() {
        throw new CustomException("custom exception");
    }

    @RequestMapping("/attr")
    public Map<String, Object> attr(Model model) {
        return (Map<String, Object>) model.getAttribute("test");
    }

    @PostMapping("/user")
    public Result setUser(@ModelAttribute("user") User user) {
        return new Result(200, user.getName() + " : " + user.getEmail());
    }
}
