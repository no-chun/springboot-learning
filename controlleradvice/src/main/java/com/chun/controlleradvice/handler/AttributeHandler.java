package com.chun.controlleradvice.handler;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AttributeHandler {
    @ModelAttribute(name = "test")
    public Map<String, Object> data() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", "chun");
        map.put("age", 18);
        return map;
    }
}
