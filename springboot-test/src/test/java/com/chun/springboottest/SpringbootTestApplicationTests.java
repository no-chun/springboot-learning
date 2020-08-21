package com.chun.springboottest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SpringbootTestApplicationTests {
    private static int num;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Before All");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("After All");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("Before Each");
    }

    @AfterEach
    void afterEach() {
        System.out.println("After Each");
    }

    @Test
    @DisplayName("Test 1")
    void Test1() {
        assertEquals(2, 1 + 1);
    }

    @Test
    @DisplayName("Test 2")
    void Test2() {
        assertTimeout(Duration.ofSeconds(1), () -> Thread.sleep(500));
    }

    @RepeatedTest(2)
    void repeatedTest() {
        System.out.println("Repeated Test");
    }

    @Test
    void exceptionTest() {
        assertThrows(Exception.class, () -> {
            throw new Exception("Error");
        });
    }

    @Test
    @Disabled
    void disabled() {
        System.out.println("This won't happen.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "b", "c"})
    void palindromes(String s) {
        assertTrue("abc".contains(s));
    }

    @Test
    @Order(1)
    void setNum() {
        num = 1;
    }

    @Test
    @Order(2)
    void testOrder() {
        assertEquals(1, num);
    }
}
