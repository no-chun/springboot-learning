package com.chun.jpademo.repository;

import com.chun.jpademo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    public void testSave() {
        User user = new User("chun", "xxxxxx", "xx@xx.com", new Date());
        userRepository.save(user);
        assertEquals("chun", userRepository.findByEmail("xx@xx.com").getUserName());
    }

    @Test
    public void testUpdate() {
        userRepository.updateUserNameById("xxxx", 1L);
    }

    @Test
    public void testDelete() {
        userRepository.deleteById(1L);
    }

    @Test
    public void testFindAll() {
        int page = 2, size = 2;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userRepository.findAll(pageable);
    }
}