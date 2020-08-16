package com.chun.springsecurityjwt.controller;

import com.chun.springsecurityjwt.model.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @RequestMapping("/index")
    public Result index() {
        return new Result(200, "ok", null);
    }

    @RequestMapping("/admin/info")
    public Result admin() {
        return new Result(200, "ok", "admin");
    }

    @RequestMapping("/user/info")
    public Result user() {
        return new Result(200, "ok", "user");
    }

}
