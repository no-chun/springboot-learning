package com.chun.thymeleaf.controller;

import com.chun.thymeleaf.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class TestController {

    private List<User> users = new ArrayList<User>();

    @RequestMapping("/users")
    public String getUser(Model model) {
        model.addAttribute("userList", users);
        return "Users";
    }

    @RequestMapping("/form")
    public String form(Model model) {
        model.addAttribute("user", new User());
        return "Add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addUser(@ModelAttribute(value = "user") User user) {
        users.add(user);
        return "redirect:/users";
    }
}
