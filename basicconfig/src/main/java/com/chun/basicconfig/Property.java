package com.chun.basicconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Property {
    @Value("${project.name}")
    private String name;

    @Value("${project.version}")
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
