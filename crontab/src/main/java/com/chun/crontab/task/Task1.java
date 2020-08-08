package com.chun.crontab.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Task1 {
    private int count = 1;

    @Scheduled(cron = "*/6 * * * * ?")
    public void process() {
        System.out.println(new Date().toString() + ":" + count++);
    }

    @Scheduled(fixedDelay = 1000)
    public void delay() {
        System.out.println(new Date().toString());
    }
}
