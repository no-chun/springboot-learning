package com.chun.jdbcdemo.service.impl;

import com.chun.jdbcdemo.dao.UserDao;
import com.chun.jdbcdemo.model.User;
import com.chun.jdbcdemo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("UserService")
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public int add(User user) {
        return userDao.add(user);
    }

    @Override
    public int update(User user) {
        return userDao.update(user);
    }

    @Override
    public int deleteById(Long id) {
        return userDao.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> queryUserListMap() {
        return userDao.queryUsersListMap();
    }

    @Override
    public User queryUserById(Long id) {
        return userDao.queryUserById(id);
    }
}
