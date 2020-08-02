package com.chun.redis.controller;

import com.chun.redis.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@RestController
public class TestController {

    @RequestMapping("/user")
    @Cacheable(value = "user-key")
    public User getUser(){
        User user = new User("test@xxx.com", "chun", "xxxxxx", "chun", "123");
        System.out.println("只会出现一次，缓存后不会出现");
        return user;
    }

    @RequestMapping("/uid")
    String uid(HttpSession session) {
        UUID uid = (UUID) session.getAttribute("uid");
        if (uid == null) {
            uid = UUID.randomUUID();
        }
        session.setAttribute("uid", uid);
        return session.getId();
    }
}
