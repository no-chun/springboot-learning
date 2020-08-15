package com.chun.springsecurityverifycode.controller;

import com.chun.springsecurityverifycode.model.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/")
    public Result index() {
        return new Result(200, "ok", null);
    }
}
