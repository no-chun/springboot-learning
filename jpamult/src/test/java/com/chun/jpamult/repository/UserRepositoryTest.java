package com.chun.jpamult.repository;

import com.chun.jpamult.model.User;
import com.chun.jpamult.repository.primaryRepository.PrimaryUserRepository;
import com.chun.jpamult.repository.secondaryRepository.SecondaryUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.util.Date;
import java.util.Optional;

@SpringBootTest
class UserRepositoryTest {

    @Resource
    private PrimaryUserRepository primaryUserRepository;

    @Resource
    private SecondaryUserRepository secondaryUserRepository;

    @Test
    void testSave() {
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        String time = dateFormat.format(date);
        primaryUserRepository.save(new User("a", "a123", "a@qq.com", "aa", time));
        primaryUserRepository.save(new User("b", "b123", "b@qq.com", "bb", time));
        secondaryUserRepository.save(new User("c", "c123", "c@qq.com", "cc", time));
    }

    @Test
    void findById() {
        Optional<User> userOptional = primaryUserRepository.findById(1L);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println(user.getUserName() + user.getEmail());
        } else {
            throw new RuntimeException("err");
        }
    }

    @Test
    void findByUserName() {
        User user = secondaryUserRepository.findByUserName("c");
        if (user != null) {
            System.out.println(user.getUserName() + user.getEmail());
        } else {
            throw new RuntimeException("err");
        }
    }

    @Test
    void findByUserNameOrEmail() {
        User user = primaryUserRepository.findByUserNameOrEmail("d", "a@qq.com");
        if (user != null) {
            System.out.println(user.getUserName() + user.getEmail());
        } else {
            throw new RuntimeException("err");
        }
    }
}