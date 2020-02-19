package com.chun.basicconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {
    @Autowired
    private Property property;

    @Value("${project.title}")
    private String title;

    @Autowired
    private TestConfigBean testConfigBean;

    @RequestMapping("/")
    String index() {
        return "project: " + property.getName() + "version ： " + property.getVersion();
    }

    @RequestMapping("title")
    String title() {
        return title;
    }

    @RequestMapping("test")
    String test() {
        return "project: " + testConfigBean.getName() + " version ： " + testConfigBean.getVersion();
    }
}
