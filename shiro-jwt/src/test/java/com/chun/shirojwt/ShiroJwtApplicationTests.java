package com.chun.shirojwt;

import com.chun.shirojwt.utils.JwtUtil;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShiroJwtApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(JwtUtil.verify("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1OTc2NDU3OTAsInVzZXJuYW1lIjoiYWRtaW4ifQ.PWYGQgxBs47tCmbABrWI6CNpx_8ffv0C49f3Q5dlRXo", "admin", "d3c59d25033dbf980d29554025c23a75"));
    }

}
