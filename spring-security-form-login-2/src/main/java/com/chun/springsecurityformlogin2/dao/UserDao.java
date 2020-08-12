package com.chun.springsecurityformlogin2.dao;

import com.chun.springsecurityformlogin2.model.User;

public interface UserDao {
    int add(User user);

    int update(User user);

    int deleteUserByName(String username);

    User findByName(String username);
}
