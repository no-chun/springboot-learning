package com.chun.springsecurityformlogin2.controller;

import com.chun.springsecurityformlogin2.dao.AuthDao;
import com.chun.springsecurityformlogin2.dao.UserDao;
import com.chun.springsecurityformlogin2.model.Authority;
import com.chun.springsecurityformlogin2.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final AuthDao authDao;

    private final UserDao userDao;

    public UserController(UserDao userDao, AuthDao authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    @PostMapping("/admin/user")
    public boolean addUser(@RequestBody User user) {
        return userDao.add(user) == 1 &&
                authDao.addAuth(new Authority(user.getUsername(), "ROLE_user")) == 1;
    }

    @PutMapping("/admin/user")
    public boolean updateUser(@RequestBody User user) {
        return userDao.update(user) == 1;
    }

    @PutMapping("/admin/auth")
    public boolean update(@RequestBody Authority authority) {
        return authDao.updateAuth(authority) == 1;
    }

    @DeleteMapping("/admin/user")
    public boolean deleteUser(@RequestParam("username") String username) {
        return authDao.deleteAuth(username) == 1 &&
                userDao.deleteUserByName(username) == 1;
    }

    @GetMapping("/admin/user")
    public User getUser(@RequestParam("username") String username) {
        return userDao.findByName(username);
    }

    @GetMapping("/admin/auth")
    public List<Authority> getAuth(@RequestParam("username") String username) {
        return authDao.getAuth(username);
    }
}
