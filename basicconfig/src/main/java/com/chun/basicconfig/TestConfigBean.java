package com.chun.basicconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@PropertySource("classpath:test.properties")
@ConfigurationProperties(prefix = "test")
public class TestConfigBean {
    private String name;
    private float version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }
}
