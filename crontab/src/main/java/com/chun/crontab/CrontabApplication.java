package com.chun.crontab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CrontabApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrontabApplication.class, args);
    }

}
