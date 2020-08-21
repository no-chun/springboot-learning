package com.chun.springboottest.service;

import com.chun.springboottest.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private List<User> users = new ArrayList<>();

    public void add(User user) {
        users.add(user);
    }

    public void delete(User user) {
        for (User u : users) {
            if (u.getId() == user.getId()) {
                users.remove(u);
                return;
            }
        }
    }

    public User getById(long id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public User getByName(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }
}
