package com.chun.redis;

import com.chun.redis.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    public void testSet() {
        stringRedisTemplate.opsForValue().set("a", "1");
        assertEquals("1", stringRedisTemplate.opsForValue().get("a"));
    }

    @Test
    public void testUser() throws InterruptedException {
        User user = new User("test@xxx.com", "chun", "xxxxxx", "chun", "123");
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        operations.set("com.chun", user);
        operations.set("com.xxx", user, 1, TimeUnit.SECONDS);
        Thread.sleep(1000);
        assertEquals("chun", operations.get("com.xxx").getUserName());
    }

}
