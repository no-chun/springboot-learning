package com.chun.jdbcdemo.dao;

import com.chun.jdbcdemo.model.User;

import java.util.List;
import java.util.Map;

public interface UserDao {
    int add(User user);

    int update(User user);

    int deleteById(Long id);

    List<Map<String, Object>> queryUsersListMap();

    User queryUserById(Long id);
}
