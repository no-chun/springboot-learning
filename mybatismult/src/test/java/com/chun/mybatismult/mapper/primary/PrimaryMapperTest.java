package com.chun.mybatismult.mapper.primary;

import com.chun.mybatismult.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PrimaryMapperTest {
    @Autowired
    private PrimaryMapper primaryMapper;

    @Test
    void getAll() {
        List<User> users = primaryMapper.getAll();
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    void getOne() {
        User user = primaryMapper.getOne(1L);
        assertNotNull(user);
        System.out.println(user.getId());
        System.out.println(user);
    }

    @Test
    void insert() {
        User user = new User();
        user.setUserName("chun");
        user.setPassWord("xxxx");
        user.setEmail("1@qq.com");
        user.setNickName("c");
        user.setRegTime(new Date().toString());
        primaryMapper.insert(user);
    }

    @Test
    void update() {
        User user = primaryMapper.getOne(1L);
        user.setNickName("x-x");
        primaryMapper.update(user);
        assertEquals("x-x", primaryMapper.getOne(1L).getNickName());
    }

    @Test
    void delete() {
        primaryMapper.delete(1L);
        User user = primaryMapper.getOne(1L);
        assertNull(user);
    }
}