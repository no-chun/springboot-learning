package com.chun.json.controller;

import com.chun.json.model.Info;
import com.chun.json.model.User;
import com.chun.json.model.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class TestController {
    private final ObjectMapper mapper;

    public TestController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @RequestMapping("/getuser")
    public User getUser() {
        User user = new User();
        user.setUsername("chun");
        user.setPassword("xxxx");
        user.setBirthday(new Date());
        return user;
    }

    @RequestMapping("/serialization")
    public String serialization() {
        User user = new User();
        user.setUsername("chun");
        user.setPassword("xxxx");
        user.setBirthday(new Date());
        try {
            return mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping("/read")
    public User readUser() {
        String userStr = "{\"name\":\"chun\",\"password\":\"xxxx\",\"birthday\":\"2020-08-19 03:37:11\"}";
        try {
            return mapper.readValue(userStr, User.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping("/getUserInfo")
    public UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername("c");
        userInfo.setEmail("test@xxx.com");
        userInfo.setPassword("xxx");
        userInfo.setRegisterTime(new Date());
        userInfo.setLastLoginTime(new Date());
        return userInfo;
    }

    @RequestMapping("/getInfo")
    public Info getInfo() {
        return new Info("hi", new Date());
    }

    @RequestMapping("/readInfo")
    public String readInfo() {
        String infoStr = "{\"message\":\"hi\",\"send_time\":\"2020-08-19 12:37:54\"}";
        try {
            Info info = mapper.readValue(infoStr, Info.class);
            return info.getMsg();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
