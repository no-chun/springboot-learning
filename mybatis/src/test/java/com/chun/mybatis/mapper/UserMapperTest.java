package com.chun.mybatis.mapper;

import com.chun.mybatis.enums.UserSexEnum;
import com.chun.mybatis.model.User;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;
    
    @Test
    void insertTest() {
        int num = userMapper.getAll().size();
        userMapper.insert(new User("a", "123456", UserSexEnum.MAN));
        userMapper.insert(new User("b", "123456", UserSexEnum.WOMAN));
        userMapper.insert(new User("c", "123456", UserSexEnum.WOMAN));
        assertEquals(num + 3, userMapper.getAll().size());
    }

    @Test
    void getAllTest() {
        List<User> users = userMapper.getAll();
        System.out.println(users);
    }

    @Test
    void getOneTest() {
        User user = userMapper.getOne((long) 1);
        assertEquals(1, user.getId());
    }

    @Test
    void updateTest() {
        User user = userMapper.getOne((long) 1);
        user.setNickName("chun");
        userMapper.update(user);
        user = userMapper.getOne((long) 1);
        assertEquals("chun", user.getNickName());
    }
}